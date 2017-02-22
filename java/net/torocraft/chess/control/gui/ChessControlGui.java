package net.torocraft.chess.control.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.control.MessageChessControl;

public class ChessControlGui extends GuiScreen {

	private final BlockPos controlBlockPos;

	private GuiButton buttonResetGame;
	private GuiButton buttonClearGame;

	public ChessControlGui(BlockPos controlBlockPos) {
		this.controlBlockPos = controlBlockPos;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		buttonList.add(buttonResetGame = new GuiButton(0, width / 2 - 100, height / 2 - 24, I18n.format("gui.chesscontrol.reset", (Object) null)));
		buttonList.add(buttonClearGame = new GuiButton(1, width / 2 - 100, height / 2 + 4, I18n.format("gui.chesscontrol.clear", (Object) null)));
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
	}

	private void closeGui() {
		mc.displayGuiScreen(null);
		if (mc.currentScreen == null) {
			mc.setIngameFocus();
		}
	}
}
