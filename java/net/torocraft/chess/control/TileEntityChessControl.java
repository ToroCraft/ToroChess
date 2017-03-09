package net.torocraft.chess.control;

import java.util.List;
import java.util.UUID;

import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.impl.ChessRuleEngine;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.gen.ChessGameGenerator;
import net.torocraft.chess.items.HighlightedChessPiecePredicate;

public class TileEntityChessControl extends TileEntity {

	private static final String NBT_SELECTED_RANK = "chessposrank";
	private static final String NBT_SELECTED_FILE = "chessposfile";
	private static final String NBT_GAME_ID = "chessgameid";
	private static final String NBT_TURN = "chessturn";
	private static final String NBT_A8 = "chessa8";

	private UUID gameId;
	private Position selectedPiece;
	private Side turn = Side.WHITE;
	private IChessRuleEngine ruleEngine;
	private BlockPos a8;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityChessControl.class, "chess_control_tile_entity");
	}

	public IChessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = new ChessRuleEngine();
		}
		return ruleEngine;
	}

	public void resetBoard() {
		if (gameId == null) {
			throw new NullPointerException("gameId is null");
		}
		if (a8 == null) {
			throw new NullPointerException("gameId is null");
		}
		System.out.println("reset board");
		clearBoard();
		ChessGameGenerator.placePieces(world, a8, gameId);
	}

	public void clearBoard() {
		System.out.println("clear board");
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece piece : pieces) {
			piece.setDead();
		}
	}

	public void forfeit() {
		// TODO
	}

	public void rewind() {
		// TODO?
	}

	public boolean movePiece(BlockPos a8, Position to) {

		if (selectedPiece == null) {
			System.out.println("No piece has been selected");
			return false;
		}

		Position from = selectedPiece;

		EntityChessPiece attacker = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (attacker == null) {
			return false;
		}

		if (isNotYourTurn(attacker)) {
			System.out.println("It's not " + attacker.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		System.out.println("Request Move:  " + from + " -> " + to);

		if (CheckerBoardUtil.isInvalidMove(gameId, a8, from, to)) {
			System.out.println("INVALID MOVE");
			return false;
		}

		EntityChessPiece victim = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (isSameSide(attacker, victim)) {
			return false;
		}

		deselectEntity();
		switchTurns();

		attacker.setAttackTarget(victim);
		attacker.setChessPosition(to);

		return true;
	}

	private boolean isNotYourTurn(EntityChessPiece attacker) {
		return !attacker.getSide().equals(turn);
	}

	private void switchTurns() {
		if (Side.WHITE.equals(turn)) {
			turn = Side.BLACK;
		} else {
			turn = Side.WHITE;
		}
		System.out.println(turn.toString().toLowerCase() + "'s trun");
	}

	private boolean isSameSide(EntityChessPiece target, EntityChessPiece victum) {
		return victum != null && victum.getSide().equals(target.getSide());
	}

	// TODO sync highlights on startup
	public boolean selectEntity(EntityChessPiece target) {
		if (target == null) {
			throw new NullPointerException("target is null");
		}

		if (target.getChessPosition().equals(selectedPiece)) {
			deselectEntity();
			markDirty();
			return true;
		}

		if (isNotYourTurn(target)) {
			System.out.println("It's not " + target.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		selectedPiece = target.getChessPosition();
		setHightlight(target);
		updateValidMoves(target);
		markDirty();
		return true;
	}

	public void deselectEntity() {
		selectedPiece = null;
		removeAllHighlights();
		markDirty();
	}

	private void setHightlight(EntityChessPiece target) {
		removeAllHighlights();
		target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false));
	}

	private void removeAllHighlights() {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new HighlightedChessPiecePredicate(gameId));
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	/*
	 * TODO this needs to happen on the client, via a packet?
	 */
	private void updateValidMoves(EntityChessPiece piece) {
		ChessMoveResult moves = getRuleEngine().getMoves(CheckerBoardUtil.loadPiecesFromWorld(piece), CheckerBoardUtil.convertToState(piece));
		if (moves.blackCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			initiateCheckmate(Side.BLACK, piece);
		} else if (moves.whiteCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			initiateCheckmate(Side.WHITE, piece);
		}
		CheckerBoardOverlay.INSTANCE.setValidMoves(moves.legalPositions);
	}

	private void initiateCheckmate(Side losingSide, EntityChessPiece piece) {
		for (ChessPieceState chessPieceState : CheckerBoardUtil.loadPiecesFromWorld(piece)) {
			EntityChessPiece chessPiece = CheckerBoardUtil.getPiece(world, chessPieceState.position, a8, gameId);
			if (chessPiece != null && !chessPiece.getSide().equals(losingSide)) {
				chessPiece.initiateWinCondition();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		if (c.hasKey(NBT_SELECTED_FILE) && c.hasKey(NBT_SELECTED_RANK)) {
			selectedPiece = new Position(File.values()[c.getInteger(NBT_SELECTED_FILE)], Rank.values()[c.getInteger(NBT_SELECTED_RANK)]);
		} else {
			selectedPiece = null;
		}
		gameId = c.getUniqueId(NBT_GAME_ID);
		turn = CheckerBoardUtil.castSide(c.getBoolean(NBT_TURN));
		a8 = BlockPos.fromLong(c.getLong(NBT_A8));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cIn) {
		NBTTagCompound c = super.writeToNBT(cIn);
		if (selectedPiece == null) {
			c.removeTag(NBT_SELECTED_FILE);
			c.removeTag(NBT_SELECTED_RANK);
		} else {
			c.setInteger(NBT_SELECTED_FILE, selectedPiece.file.ordinal());
			c.setInteger(NBT_SELECTED_RANK, selectedPiece.rank.ordinal());
		}

		if (gameId != null) {
			c.setUniqueId(NBT_GAME_ID, gameId);
		}

		c.setBoolean(NBT_TURN, CheckerBoardUtil.castSide(turn));

		if (a8 != null) {
			c.setLong(NBT_A8, a8.toLong());
		}

		return c;
	}

	public UUID getGameId() {
		return gameId;
	}

	public Position getSelectedPiece() {
		return selectedPiece;
	}

	public Side getTurn() {
		return turn;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
		markDirty();
	}

	public void setSelectedPiece(Position selectedPiece) {
		this.selectedPiece = selectedPiece;
		markDirty();
	}

	public void setTurn(Side turn) {
		this.turn = turn;
		markDirty();
	}

	public void setRuleEngine(IChessRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
		markDirty();
	}

	public BlockPos getA8() {
		return a8;
	}

	public void setA8(BlockPos a8) {
		this.a8 = a8;
		markDirty();
	}

}
