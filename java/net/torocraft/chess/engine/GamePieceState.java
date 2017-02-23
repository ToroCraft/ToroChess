package net.torocraft.chess.engine;

public abstract class GamePieceState {
    public Position position;
    public Side side;
    public boolean isInitialMove;

	public enum Side {
		WHITE, BLACK
	}

	public enum File {
		A, B, C, D, E, F, G, H
	}

	public enum Rank {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
	}

	public static class Position {
		public File file;
		public Rank rank;

		public Position(File file, Rank rank) {
			if (file == null) {
				throw new NullPointerException("file is null");
			}
			if (rank == null) {
				throw new NullPointerException("rank is null");
			}
			this.file = file;
			this.rank = rank;
		}

		public Position(Position position) {
		    this.file = position.file;
		    this.rank = position.rank;
        }

		@Override
		public String toString() {
			return file.toString().toLowerCase() + (rank.ordinal() + 1);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((file == null) ? 0 : file.hashCode());
			result = prime * result + ((rank == null) ? 0 : rank.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Position other = (Position) obj;
			if (file != other.file) {
				return false;
			}
			if (rank != other.rank) {
				return false;
			}
			return true;
		}

	}

	public static class Move {
		public Position currentPosition;
		public Position requestedMoveToPosition;

		public Move(Position currentPosition, Position requestedMoveToPosition) {
			this.currentPosition = currentPosition;
			this.requestedMoveToPosition = requestedMoveToPosition;
		}
	}

}
