package net.torocraft.chess.control.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.control.MessageChessControl;
import net.torocraft.chess.control.MessageChessSetPlayMode;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.control.TileEntityChessControl.PlayMode;
import net.torocraft.chess.engine.GamePieceState.Side;

public class ChessControlGui extends GuiScreen {

  private final BlockPos controlBlockPos;
  private final World world;

  private TileEntityChessControl control;

  private GuiButton buttonResetGame;
  private GuiButton buttonClearGame;

  private PlayModeButton whitePlayerMode;
  private PlayModeButton blackPlayerMode;

  public ChessControlGui(World world, BlockPos controlBlockPos) {
    this.controlBlockPos = controlBlockPos;
    this.world = world;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    int wCenter = width / 2;
    int hCenter = height / 2;
    drawString(fontRenderer, I18n.format("gui.chesscontrol.white_mode", (Object) null), wCenter - 100, hCenter + 35, 0xffffff);
    drawString(fontRenderer, I18n.format("gui.chesscontrol.black_mode", (Object) null), wCenter - 100, hCenter + 55, 0xffffff);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {

    getChessControl();

    if (control == null) {
      return;
    }

    int buttonId = 0;

    int wCenter = width / 2;
    int hCenter = height / 2;

    buttonList
        .add(buttonResetGame = new GuiButton(buttonId++, wCenter - 100, hCenter - 24, I18n.format("gui.chesscontrol.reset", (Object) null)));
    buttonList.add(buttonClearGame = new GuiButton(buttonId++, wCenter - 100, hCenter + 4, I18n.format("gui.chesscontrol.clear", (Object) null)));

    whitePlayerMode = new PlayModeButton(buttonId++, wCenter + 10, hCenter + 30, Side.WHITE, controlBlockPos);

    whitePlayerMode.setMode(control.getWhitePlayMode());

    blackPlayerMode = new PlayModeButton(buttonId++, wCenter + 10, hCenter + 50, Side.BLACK, controlBlockPos);

    blackPlayerMode.setMode(control.getBlackPlayMode());

    buttonList.add(whitePlayerMode);
    buttonList.add(blackPlayerMode);

  }

  private void getChessControl() {

    if (world.getBlockState(controlBlockPos).getBlock() != BlockChessControl.INSTANCE) {
      return;
    }

    TileEntity te = world.getTileEntity(controlBlockPos);

    if (te == null || !(te instanceof TileEntityChessControl)) {
      return;
    }

    control = (TileEntityChessControl) te;
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    if (button == buttonResetGame) {
      ToroChess.NETWORK.sendToServer(new MessageChessControl(controlBlockPos, MessageChessControl.COMMAND_RESET));
      closeGui();
    }

    if (button == buttonClearGame) {
      ToroChess.NETWORK.sendToServer(new MessageChessControl(controlBlockPos, MessageChessControl.COMMAND_CLEAR));
      closeGui();
    }

    if (button == whitePlayerMode) {
      whitePlayerMode.actionPerformed();
    }

    if (button == blackPlayerMode) {
      blackPlayerMode.actionPerformed();
    }
  }

  private void closeGui() {
    mc.displayGuiScreen(null);
    if (mc.currentScreen == null) {
      mc.setIngameFocus();
    }
  }

  private static class PlayModeButton extends GuiButton {

    private final Side side;
    private final BlockPos controlBlockPos;
    private PlayMode mode = PlayMode.PLAYER;

    public PlayModeButton(int buttonId, int x, int y, Side side, BlockPos controlBlockPos) {
      this(buttonId, x, y, 90, 20, side, controlBlockPos);
    }

    public PlayModeButton(int buttonId, int x, int y, int widthIn, int heightIn, Side side, BlockPos controlBlockPos) {
      super(buttonId, x, y, widthIn, heightIn, "");
      updateDisplayString();
      this.side = side;
      this.controlBlockPos = controlBlockPos;
    }

    public void setMode(PlayMode mode) {
      this.mode = mode;
      updateDisplayString();
    }

    private void updateDisplayString() {
      displayString = mode.toString();
    }

    public void actionPerformed() {
      nextValue();
      ToroChess.NETWORK.sendToServer(new MessageChessSetPlayMode(controlBlockPos, side, mode));
    }

    private void nextValue() {
      int i = mode.ordinal() + 1;
      int max = PlayMode.values().length - 1;

      if (i > max) {
        i = 0;
      }
      setMode(PlayMode.values()[i]);
    }
  }

}
