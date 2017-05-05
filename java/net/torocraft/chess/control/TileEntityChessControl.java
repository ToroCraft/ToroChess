package net.torocraft.chess.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.ToroChessEvent.MoveEvent;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Move;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.engine.chess.CastleMove;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessAIEngine;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.impl.ChessRuleEngine;
import net.torocraft.chess.engine.chess.impl.RandomAIEngine;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.gen.ChessGameGenerator;
import net.torocraft.chess.items.HighlightedChessPiecePredicate;

public class TileEntityChessControl extends TileEntity implements ITickable {

	private static final String NBT_SELECTED_RANK = "chessposrank";
	private static final String NBT_SELECTED_FILE = "chessposfile";
	private static final String NBT_GAME_ID = "chessgameid";
	private static final String NBT_TURN = "chessturn";
	private static final String NBT_A8 = "chessa8";
	private static final String NBT_WHITE_MODE = "whitemode";
	private static final String NBT_BLACK_MODE = "blackmode";

	private UUID gameId;
	private Position selectedPiece;
	private Side turn = Side.WHITE;
	private IChessRuleEngine ruleEngine;
	private IChessAIEngine aiEngine;
	private BlockPos a8;
	private ChessMoveResult moves;
	private int fireworksRunCounter = -1;
	private int turnBellCounter = -1;
	private int turnBellTimes = 0;
	private boolean resetOnClear = false;
	private PlayMode whitePlayMode = PlayMode.PLAYER;
	private PlayMode blackPlayMode = PlayMode.EASY;
	private boolean caslteInProgress = false;

	private List<ITask> runQueue = new ArrayList<>();

	public static enum PlayMode {
		PLAYER, EASY
	};

	private int clearBoardTimer = -1;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityChessControl.class, "chess_control_tile_entity");
	}

	public TileEntityChessControl() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public IChessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = new ChessRuleEngine();
		}
		return ruleEngine;
	}

	public IChessAIEngine getAiEngine() {
		if (aiEngine == null) {
			aiEngine = new RandomAIEngine();
		}
		return aiEngine;
	}

	public void resetBoard() {
		if (gameId == null) {
			throw new NullPointerException("gameId is null");
		}
		if (a8 == null) {
			throw new NullPointerException("gameId is null");
		}

		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece chessPiece : pieces) {
			chessPiece.setClearCondition();
		}

		resetOnClear = true;
		winCondition = false;
		aiInProgress = false;

		clearBoard();
	}

	private List<EntityChessPiece> piecesToPlace;

	private void reset() {
		turn = Side.WHITE;
		piecesToPlace = ChessGameGenerator.genPieces(world, a8, gameId);
	}

	public void clearBoard() {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(gameId));

		for (EntityChessPiece chessPiece : pieces) {
			chessPiece.setAttackAllMode();
		}

		if (pieces.size() < 1 && resetOnClear) {
			reset();
		}

		clearBoardTimer = 50;
		winCondition = false;
		aiInProgress = false;

	}

	public boolean castlePiece(BlockPos a8, Position to) {
		if (selectedPiece == null) {
			return false;
		}

		Position from = selectedPiece;
		EntityChessPiece king = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (king == null) {
			return false;
		}

		if (!(king instanceof EntityKing)) {
			return false;
		}

		if (isNotYourTurn(king)) {
			// System.out.println("It's not " +
			// king.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		EntityChessPiece rook = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (rook == null || !(rook instanceof EntityRook)) {
			return false;
		}

		if (!isSameSide(king, rook)) {
			return false;
		}

		// System.out.println("Request Castle: " + from + " -> " + to);

		if (moves == null) {
			updateValidMoves(king);
		}

		if (moves == null) {
			return false;
		}

		if (moves.kingSideCastleMove != null) {
			if (moves.kingSideCastleMove.positionOfRook.equals(rook.getChessPosition())) {
				startCastle(moves.kingSideCastleMove, rook, king);
				return true;
			}
		}

		if (moves.queenSideCastleMove != null) {
			if (moves.queenSideCastleMove.positionOfRook.equals(rook.getChessPosition())) {
				startCastle(moves.queenSideCastleMove, rook, king);
				return true;
			}
		}

		return false;
	};

	private void startCastle(CastleMove castleMove, EntityChessPiece rook, EntityChessPiece king) {
		caslteInProgress = true;
		deselectEntity();
		rook.setChessPosition(castleMove.positionToMoveRookTo);
		king.setChessPosition(castleMove.positionToMoveKingTo);
		switchTurns();
	}

	private boolean moveInProgress = false;

	public boolean movePiece(BlockPos a8, Position to) {

		if (moveInProgress) {
			return false;
		}

		if (selectedPiece == null) {
			return false;
		}

		Position from = selectedPiece;

		EntityChessPiece attacker = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (attacker == null) {
			return false;
		}

		if (isNotYourTurn(attacker)) {
			// System.out.println("It's not " +
			// attacker.getSide().toString().toLowerCase() + "'s turn!");
			return false;
		}

		// System.out.println("Request Move: " + from + " -> " + to);

		if (isInvalidMove(gameId, a8, from, to)) {
			// System.out.println("INVALID MOVE");
			return false;
		}

		EntityChessPiece victim = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (isSameSide(attacker, victim)) {
			return false;
		}

		deselectEntity();
		switchTurns();

		moveInProgress = true;

		attacker.setAttackTarget(victim);
		attacker.setChessPosition(to);

		return true;
	}

	public boolean isInvalidMove(UUID gameId, BlockPos a8, Position from, Position to) {
		if (from == null || to == null) {
			return true;
		}

		if (moves == null) {
			return false;
		}

		for (Position move : moves.legalPositions) {
			if (move.equals(to)) {
				return false;
			}
		}
		return true;
	}

	@SubscribeEvent
	public void onMoveFinish(MoveEvent.Finish event) {
		if (gameId == null || !gameId.equals(event.getGameId())) {
			return;
		}

		if (caslteInProgress && event.getPiece() instanceof EntityRook) {
			/*
			 * ignore the move finish event from the rook during a castle
			 */
			caslteInProgress = false;
			return;
		}

		moveInProgress = false;

		Side thisSide = event.getPiece().getSide();
		Side otherSide = otherSide(thisSide);
		List<ChessPieceState> boardState = CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8);

		moves = getRuleEngine().getBoardConditionForSide(boardState, otherSide);
		handleBoardCondition();

		if (isPlayerMode()) {
			playTurnSwitchBell();
		}
	}

	private boolean isPlayerMode() {
		if (Side.WHITE.equals(turn)) {
			return PlayMode.PLAYER.equals(whitePlayMode) || whitePlayMode == null;
		} else {
			return PlayMode.PLAYER.equals(blackPlayMode) || blackPlayMode == null;
		}
	}

	private void playTurnSwitchBell() {
		turnBellTimes = 2;
		turnBellCounter = 12;
	}

	private Side otherSide(Side thisSide) {
		if (Side.WHITE.equals(thisSide)) {
			return Side.BLACK;
		} else {
			return Side.WHITE;
		}
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
		sendTurnChangeMessage();
	}

	private void sendTurnChangeMessage() {
		TargetPoint p = new TargetPoint(world.provider.getDimension(), a8.getX() + 4, a8.getY(), a8.getZ() + 4, 40);
		ToroChess.NETWORK.sendToAllAround(new MessageTurnChangeEvent(turn, gameId), p);
	}

	private boolean isSameSide(EntityChessPiece target, EntityChessPiece victum) {
		return victum != null && victum.getSide().equals(target.getSide());
	}

	public boolean selectEntity(EntityChessPiece target) {
		if (target == null) {
			return false;
		}

		if (target.getChessPosition().equals(selectedPiece)) {
			deselectEntity();
			markDirty();
			return true;
		}

		if (isNotYourTurn(target)) {
			// System.out.println("It's not " +
			// target.getSide().toString().toLowerCase() + "'s turn!");
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

	private void updateValidMoves(EntityChessPiece piece) {
		moves = null;

		ChessPieceState thisPiece = CheckerBoardUtil.convertToState(piece);
		List<ChessPieceState> allPieces = CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8);

		moves = getRuleEngine().getMoves(allPieces, thisPiece);

		if (moves == null) {
			return;
		}

		handleBoardCondition();
	}

	private boolean winCondition = false;

	private void handleBoardCondition() {
		if (moves == null || moves.blackCondition == null || moves.whiteCondition == null) {
			return;
		}

		if (moves.blackCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			winCondition = true;
			initiateCheckmate(Side.BLACK);
			return;
		} else if (moves.whiteCondition.equals(ChessMoveResult.Condition.CHECKMATE)) {
			initiateCheckmate(Side.WHITE);
			winCondition = true;
			return;
		}

		if (moves.blackCondition.equals(ChessMoveResult.Condition.STALEMATE)) {
			winCondition = true;
			initiateStalemate(Side.BLACK);
			return;
		} else if (moves.whiteCondition.equals(ChessMoveResult.Condition.STALEMATE)) {
			initiateStalemate(Side.WHITE);
			winCondition = true;
			return;
		}

		EntityKing whiteKing = getKing(Side.WHITE);
		EntityKing blackKing = getKing(Side.BLACK);

		if (whiteKing != null) {
			whiteKing.setInCheck(moves.whiteCondition.equals(ChessMoveResult.Condition.CHECK));
		}
		if (blackKing != null) {
			blackKing.setInCheck(moves.blackCondition.equals(ChessMoveResult.Condition.CHECK));
		}
	}

	private void initiateStalemate(Side black) {
		// TODO Auto-generated method stub
		System.out.println("Stalemate!");
	}

	private EntityKing getKing(Side side) {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos.add(4, 0, 4)).expand(80, 20, 80),
				Predicates.and(new ChessPieceSearchPredicate(gameId), new KingSelector(side)));

		return pieces == null || pieces.size() < 1 ? null : (EntityKing) pieces.get(0);
	}

	private EntityChessPiece getPieceEntityAt(Side side, Position position) {
		if (position == null) {
			throw new NullPointerException("position is null");
		}
		if (side == null) {
			throw new NullPointerException("side is null");
		}
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos.add(4, 0, 4)).expand(80, 20, 80),
				Predicates.and(new ChessPieceSearchPredicate(gameId), new Predicate<EntityChessPiece>() {
					@Override
					public boolean apply(EntityChessPiece p) {
						return position.equals(p.getChessPosition()) && side.equals(p.getSide());
					}
				}));

		return pieces == null || pieces.size() < 1 ? null : pieces.get(0);
	}

	private void initiateCheckmate(Side losingSide) {
		for (ChessPieceState chessPieceState : CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8)) {
			EntityChessPiece chessPiece = CheckerBoardUtil.getPiece(world, chessPieceState.position, a8, gameId);
			if (chessPiece != null && !chessPiece.getSide().equals(losingSide)) {
				chessPiece.setAttackAllMode();
			}
		}
		startFireworkDisplay();
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		readFromNBTLocal(c);
	}

	public void readFromNBTLocal(NBTTagCompound c) {
		if (c.hasKey(NBT_SELECTED_FILE) && c.hasKey(NBT_SELECTED_RANK)) {
			selectedPiece = new Position(File.values()[c.getInteger(NBT_SELECTED_FILE)], Rank.values()[c.getInteger(NBT_SELECTED_RANK)]);
		} else {
			selectedPiece = null;
		}
		gameId = c.getUniqueId(NBT_GAME_ID);
		turn = CheckerBoardUtil.castSide(c.getBoolean(NBT_TURN));
		a8 = BlockPos.fromLong(c.getLong(NBT_A8));
		whitePlayMode = PlayMode.values()[c.getInteger(NBT_WHITE_MODE)];
		blackPlayMode = PlayMode.values()[c.getInteger(NBT_BLACK_MODE)];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cIn) {
		NBTTagCompound c = super.writeToNBT(cIn);
		writeToNBTLocal(c);
		return c;
	}

	public NBTTagCompound writeToNBTLocal(NBTTagCompound c) {
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

		c.setInteger(NBT_WHITE_MODE, whitePlayMode.ordinal());
		c.setInteger(NBT_BLACK_MODE, blackPlayMode.ordinal());

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

	private static class KingSelector implements Predicate<EntityChessPiece> {
		private final Side side;

		public KingSelector(Side side) {
			this.side = side;
		}

		@Override
		public boolean apply(EntityChessPiece piece) {
			return piece != null && piece instanceof EntityKing && side.equals(piece.getSide());
		}
	}

	private void buildFirework(World world, BlockPos pos) {
		ItemStack item = new ItemStack(Items.FIREWORKS);

		NBTTagCompound explosion1 = buildFireworkExplosive(world.rand);

		NBTTagList explosions = new NBTTagList();
		explosions.appendTag(explosion1);

		NBTTagCompound fireworks = new NBTTagCompound();
		fireworks.setTag("Explosions", explosions);
		fireworks.setInteger("Flight", 0 + world.rand.nextInt(2));

		NBTTagCompound c = new NBTTagCompound();
		c.setTag("Fireworks", fireworks);

		item.setTagCompound(c);

		EntityFireworkRocket firework = new EntityFireworkRocket(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, item);

		world.spawnEntity(firework);
	}

	private NBTTagCompound buildFireworkExplosive(Random rand) {
		NBTTagCompound explosion1 = new NBTTagCompound();
		explosion1.setBoolean("Flicker", true);
		explosion1.setBoolean("Trail", true);
		explosion1.setInteger("Type", rand.nextInt(5));
		explosion1.setIntArray("Colors", new int[] { getColor(rand) });
		return explosion1;
	}

	private int getColor(Random rand) {
		switch (rand.nextInt(6)) {
		case 0:
			return 0xFF0000;
		case 1:
			return 0x00FF00;
		case 2:
			return 0xFFFF00;
		case 3:
			return 0x00FFFF;
		case 4:
			return 0xFF00FF;
		case 5:
			return 0x0000FF;
		}
		return 0xFF0000;
	}

	private void startFireworkDisplay() {
		fireworksRunCounter = 0;
	}

	@Override
	public void update() {
		updateFireworks();
		updateTurnBell();
		updateBoardClear();
		updatePlacePieces();
		updateRunQueue();
		updateAi();
	}

	private boolean aiInProgress = false;

	private void updateAi() {
		if (world.getTotalWorldTime() % 20 != 0 || isPlayerMode() || moveInProgress || winCondition || aiInProgress) {
			return;
		}

		if ((piecesToPlace != null && piecesToPlace.size() > 0) || clearBoardTimer > -1) {
			return;
		}

		aiInProgress = true;

		final List<ChessPieceState> state = CheckerBoardUtil.loadPiecesFromWorld(world, gameId, a8);

		final Move move = getAiEngine().getAIMove(state, turn);

		if (move == null) {
			/*
			 * if no move was returned select the king to check for a win
			 * condition
			 */
			selectEntity(getKing(turn));
			aiInProgress = false;
			return;
		}

		final EntityChessPiece pieceToMove = getPieceEntityAt(turn, move.currentPosition);

		if (pieceToMove == null) {
			selectEntity(getKing(turn));
			aiInProgress = false;
			return;
		}

		runQueue.add(new TimedTask(10) {
			@Override
			public void run() {
				selectEntity(pieceToMove);
				runQueue.add(new TimedTask(10) {
					@Override
					public void run() {
						moves = new ChessMoveResult();
						moves.legalPositions = new ArrayList<>();
						moves.legalPositions.add(move.requestedMoveToPosition);
						movePiece(a8, move.requestedMoveToPosition);
						markDirty();
						aiInProgress = false;
					}
				});
			}
		});
	}

	private void updateRunQueue() {
		if (runQueue.size() < 1) {
			return;
		}

		for (Iterator<ITask> iterator = runQueue.iterator(); iterator.hasNext();) {
			ITask e = iterator.next();
			if (e.isReady()) {
				iterator.remove();
				e.run();
				return;
			}
		}
	}

	private int placePiecesTimer = 0;

	private void updatePlacePieces() {
		if (piecesToPlace == null || piecesToPlace.size() < 1) {
			placePiecesTimer = 0;
			return;
		}

		placePiecesTimer++;

		for (Iterator<EntityChessPiece> iterator = piecesToPlace.iterator(); iterator.hasNext();) {
			EntityChessPiece e = iterator.next();
			if (world.rand.nextInt(5) == 0 || placePiecesTimer > 50) {
				world.spawnEntity(e);
				playSound(SoundEvents.BLOCK_SNOW_HIT);
				iterator.remove();
				return;
			}
		}

	}

	private void updateBoardClear() {
		if (clearBoardTimer < 0) {
			return;
		}

		clearBoardTimer--;

		if (clearBoardTimer > 2) {
			return;
		}

		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new ChessPieceSearchPredicate(gameId));

		if (pieces.size() < 1) {

			if (resetOnClear) {
				reset();
				resetOnClear = false;
			}

			clearBoardTimer = -1;
			return;
		}

		clearBoardTimer = 4;

		for (EntityChessPiece piece : pieces) {
			if (world.rand.nextInt(8) == 0) {
				piece.attackEntityFrom(DamageSource.FALL, 10);
			}
		}

	}

	private void updateTurnBell() {
		if (turnBellCounter < 0) {
			return;
		}

		turnBellCounter--;

		if (turnBellCounter < 2) {
			playSound(SoundEvents.BLOCK_NOTE_HARP);
			if (turnBellTimes > 1) {
				turnBellTimes--;
				turnBellCounter = 5;
			} else {
				turnBellCounter = -1;
			}
		}
	}

	private void playSound(SoundEvent sound) {
		world.playSound((EntityPlayer) null, a8.getX() + 4, a8.getY() + 2, a8.getZ() + 4, sound, SoundCategory.NEUTRAL, 1f, 1f);
	}

	private void updateFireworks() {
		if (fireworksRunCounter < 0) {
			return;
		}

		if (world.getTotalWorldTime() % (2 + world.rand.nextInt(10)) != 0) {
			return;
		}

		buildFirework(world, a8.add(world.rand.nextInt(8), 0, world.rand.nextInt(8)));

		fireworksRunCounter++;

		if (fireworksRunCounter > 30) {
			fireworksRunCounter = -1;
		}
	}

	public ChessMoveResult getMoves() {
		return moves;
	}

	private static interface ITask extends Runnable {
		boolean isReady();
	}

	private abstract static class TimedTask implements ITask {

		private int delay;

		public TimedTask(int delay) {
			this.delay = delay;
		}

		@Override
		public boolean isReady() {
			return delay-- < 1;
		}

	}

	public PlayMode getWhitePlayMode() {
		return whitePlayMode;
	}

	public void setWhitePlayMode(PlayMode whitePlayMode) {
		this.whitePlayMode = whitePlayMode;
		markDirty();
		sendControlBlockUpdatePacket();
	}

	public PlayMode getBlackPlayMode() {
		return blackPlayMode;
	}

	public void setBlackPlayMode(PlayMode blackPlayMode) {
		this.blackPlayMode = blackPlayMode;
		markDirty();
		sendControlBlockUpdatePacket();
	}

	private void sendControlBlockUpdatePacket() {
		TargetPoint p = new TargetPoint(world.provider.getDimension(), a8.getX() + 4, a8.getY(), a8.getZ() + 4, 100);
		ToroChess.NETWORK.sendToAllAround(new MessageUpdateControlBlock(pos, writeToNBT(new NBTTagCompound())), p);
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	public NBTTagCompound getUpdateTag() {
		return writeToNBTLocal(super.getUpdateTag());
	}

}
