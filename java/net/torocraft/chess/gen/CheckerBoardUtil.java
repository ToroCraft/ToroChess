package net.torocraft.chess.gen;

import net.minecraft.util.math.BlockPos;

public class CheckerBoardUtil {

	/**
	 * Get the Minecraft coordinates for a given chess position (such as a1, e5)
	 */
	public static BlockPos getPosition(BlockPos a8, String chessPosition) {
		if(a8 == null){
			throw new NullPointerException("a8 is null");
		}
		if(chessPosition == null){
			throw new NullPointerException("chess position string is null");
		}
		int[] parsed = CheckerBoardUtil.parseIntPosition(chessPosition);
		return a8.add(parsed[0], 1, parsed[1]);
	}

	public static int[] parseIntPosition(String name) {
		int[] p = { -10, -10 };

		if (name == null || name.length() != 2) {
			return p;
		}

		name = name.toLowerCase();

		if (!name.matches("[a-h][1-8]")) {
			return p;
		}

		p[0] = parseColumnName(name.substring(0, 1));
		p[1] = i(name.substring(1, 2)) - 1;
		return p;
	}

	private static int i(String substring) {
		try {
			return Integer.valueOf(substring, 10);
		} catch (Exception e) {
			return -10;
		}
	}

	private static int parseColumnName(String s) {
		if (s == null || s.length() != 1) {
			return -10;
		}

		if (s.equals("a")) {
			return 0;
		} else if (s.equals("b")) {
			return 1;
		} else if (s.equals("c")) {
			return 2;
		} else if (s.equals("d")) {
			return 3;
		} else if (s.equals("e")) {
			return 4;
		} else if (s.equals("f")) {
			return 5;
		} else if (s.equals("g")) {
			return 6;
		} else if (s.equals("h")) {
			return 7;
		}

		return -10;
	}

	public static String getPositionName(BlockPos a8, BlockPos coords) {
		int xLocal = coords.getX() - a8.getX();
		int zLocal = coords.getZ() - a8.getZ();
		String name = encodeColumnName(xLocal) + minMax(zLocal + 1, 1, 8);
		return name;
	}

	private static int minMax(int i, int min, int max) {
		if (i > max) {
			return max;
		}

		if (i < min) {
			return min;
		}

		return i;
	}

	private static String encodeColumnName(int i) {
		switch (i) {
		case 0:
			return "a";
		case 1:
			return "b";
		case 2:
			return "c";
		case 3:
			return "d";
		case 4:
			return "e";
		case 5:
			return "f";
		case 6:
			return "g";
		case 7:
			return "h";
		}
		return "a";
	}

}
