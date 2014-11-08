/**
 * New BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 * Copyright 2009-2011 RaptorProject (http://code.google.com/p/raptor-chess-interface/)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the RaptorProject nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package raptor.swt.chess.layout;

import raptor.util.RaptorLogger;
 
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import raptor.Raptor;
import raptor.pref.PreferenceKeys;
import raptor.swt.SWTUtils;
import raptor.swt.chess.ChessBoard;
import raptor.swt.chess.ChessBoardLayout;

public class TopBottomOrientedLayout extends ChessBoardLayout {
	private static final RaptorLogger LOG = RaptorLogger.getLog(TopBottomOrientedLayout.class);

	public static final int[] BOARD_WIDTH_MARGIN_PERCENTAGES = { 2, 1 };

	public static final int[] TOP_BOTTOM_MARGIN_PERCENTAGES = { 2, 1 };

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 1;
	public static final int WEST = 0;

	protected Point boardTopLeft;
	protected Rectangle bottomClockRect;
	protected Rectangle bottomLagRect;
	protected Rectangle bottomNameLabelRect;
	protected Point bottomPieceJailTopLeft;
	protected ControlListener controlListener;
	protected Rectangle currentPremovesLabelRect;
	protected Rectangle gameDescriptionLabelRect;
	protected boolean hasHeightProblem = false;
	protected boolean hasSevereHeightProblem = false;
	protected Rectangle openingDescriptionLabelRect;
	protected int pieceJailSquareSize;
	protected int squareSize;
	protected Rectangle statusLabelRect;
	protected Rectangle topClockRect;
	protected Rectangle topLagRect;
	protected Rectangle topNameLabelRect;
	protected Point topPieceJailTopLeft;

	public TopBottomOrientedLayout(ChessBoard board) {
		super(board);
		board.getBoardComposite().addControlListener(
				controlListener = new ControlListener() {

					public void controlMoved(ControlEvent e) {
					}

					public void controlResized(ControlEvent e) {
						setLayoutData();
					}
				});
	}

	@Override
	public void adjustFontSizes() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adjusting font sizes.");
		}
		board.getGameDescriptionLabel().setFont(
				getFont(PreferenceKeys.BOARD_GAME_DESCRIPTION_FONT));
		board.getCurrentPremovesLabel().setFont(
				getFont(PreferenceKeys.BOARD_PREMOVES_FONT));
		board.getStatusLabel().setFont(
				getFont(PreferenceKeys.BOARD_STATUS_FONT));
		board.getOpeningDescriptionLabel().setFont(
				getFont(PreferenceKeys.BOARD_OPENING_DESC_FONT));
		board.getWhiteNameRatingLabel().setFont(
				getFont(PreferenceKeys.BOARD_PLAYER_NAME_FONT));
		board.getBlackNameRatingLabel().setFont(
				getFont(PreferenceKeys.BOARD_PLAYER_NAME_FONT));
		board.getWhiteLagLabel()
				.setFont(getFont(PreferenceKeys.BOARD_LAG_FONT));
		board.getBlackLagLabel()
				.setFont(getFont(PreferenceKeys.BOARD_LAG_FONT));
		board.getWhiteClockLabel().setFont(
				getFont(PreferenceKeys.BOARD_CLOCK_FONT));
		board.getBlackClockLabel().setFont(
				getFont(PreferenceKeys.BOARD_CLOCK_FONT));
	}

	@Override
	public void dispose() {
		super.dispose();
		if (!board.isDisposed()) {
			board.getBoardComposite().removeControlListener(controlListener);
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("Dispoed TopBottomOrientedLayout");
		}
	}

	@Override
	public int getAlignment(Field field) {
		switch (field) {
		case GAME_DESCRIPTION_LABEL:
			return SWT.LEFT;
		case CURRENT_PREMOVE_LABEL:
			return SWT.LEFT;
		case STATUS_LABEL:
			return SWT.LEFT;
		case OPENING_DESCRIPTION_LABEL:
			return SWT.LEFT;
		case NAME_RATING_LABEL:
			return SWT.LEFT;
		case CLOCK_LABEL:
			return SWT.RIGHT;
		case LAG_LABEL:
			return SWT.LEFT;
		case UP_TIME_LABEL:
			return SWT.LEFT | SWT.BORDER;
		default:
			return SWT.NONE;
		}
	}

	@Override
	public String getName() {
		return "Top Bottom Oriented";
	}

	protected Font getFont(String property) {
		return Raptor.getInstance().getPreferences().getFont(property);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("in layout(" + flushCache + ") " + composite.getSize().x
					+ " " + composite.getSize().y);
		}

		if (flushCache) {
			setLayoutData();
		}

		long startTime = System.currentTimeMillis();

		board.getGameDescriptionLabel().setBounds(gameDescriptionLabelRect);
		board.getCurrentPremovesLabel().setBounds(currentPremovesLabelRect);
		board.getStatusLabel().setBounds(statusLabelRect);
		board.getOpeningDescriptionLabel().setBounds(
				openingDescriptionLabelRect);

		layoutChessBoard(boardTopLeft, squareSize);

		board.getWhiteNameRatingLabel().setBounds(
				board.isWhiteOnTop() ? topNameLabelRect : bottomNameLabelRect);
		board.getWhiteLagLabel().setBounds(
				board.isWhiteOnTop() ? topLagRect : bottomLagRect);
		board.getWhiteClockLabel().setBounds(
				board.isWhiteOnTop() ? topClockRect : bottomClockRect);

		board.getBlackNameRatingLabel().setBounds(
				board.isWhiteOnTop() ? bottomNameLabelRect : topNameLabelRect);
		board.getBlackLagLabel().setBounds(
				board.isWhiteOnTop() ? bottomLagRect : topLagRect);
		board.getBlackClockLabel().setBounds(
				board.isWhiteOnTop() ? bottomClockRect : topClockRect);

		if (board.isWhitePieceJailOnTop()) {
			board.getPieceJailSquares()[WP].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[WN].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y + pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WB].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y + 2 * pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WQ].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WR].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y
					+ pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[WK].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y + 2
					* +pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);

			board.getPieceJailSquares()[BP].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[BN].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y + pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BB].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y + 2 * pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BQ].setBounds(bottomPieceJailTopLeft.x
					+ pieceJailSquareSize, bottomPieceJailTopLeft.y,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BR].setBounds(bottomPieceJailTopLeft.x
					+ pieceJailSquareSize, bottomPieceJailTopLeft.y
					+ pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[BK].setBounds(bottomPieceJailTopLeft.x
					+ squareSize, bottomPieceJailTopLeft.y + 2
					* pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
		} else {
			board.getPieceJailSquares()[BP].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[BN].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y + pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BB].setBounds(topPieceJailTopLeft.x,
					topPieceJailTopLeft.y + 2 * pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BQ].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[BR].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y
					+ pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[BK].setBounds(topPieceJailTopLeft.x
					+ pieceJailSquareSize, topPieceJailTopLeft.y + 2
					* pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);

			board.getPieceJailSquares()[WP].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[WN].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y + pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WB].setBounds(bottomPieceJailTopLeft.x,
					bottomPieceJailTopLeft.y + 2 * pieceJailSquareSize,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WQ].setBounds(bottomPieceJailTopLeft.x
					+ pieceJailSquareSize, bottomPieceJailTopLeft.y,
					pieceJailSquareSize, pieceJailSquareSize);
			board.getPieceJailSquares()[WR].setBounds(bottomPieceJailTopLeft.x
					+ pieceJailSquareSize, bottomPieceJailTopLeft.y
					+ pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
			board.getPieceJailSquares()[WK].setBounds(bottomPieceJailTopLeft.x
					+ pieceJailSquareSize, bottomPieceJailTopLeft.y + 2
					* pieceJailSquareSize, pieceJailSquareSize,
					pieceJailSquareSize);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Layout completed in "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	protected void setLayoutData() {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Setting layout data.");
		}

		int width = board.getBoardComposite().getSize().x;
		int height = board.getBoardComposite().getSize().y;

		hasHeightProblem = false;
		hasSevereHeightProblem = false;

		if (width < height) {
			height = width;
			hasHeightProblem = true;
		}

		int topBottomNorthMargin = height
				* TOP_BOTTOM_MARGIN_PERCENTAGES[NORTH] / 100;

		int topBottomSouthMargin = height
				* TOP_BOTTOM_MARGIN_PERCENTAGES[SOUTH] / 100;

		int topBottomLabelHeight = Math.max(SWTUtils.getHeightInPixels(board
				.getWhiteNameRatingLabel().getFont().getFontData()[0]
				.getHeight()), SWTUtils.getHeightInPixels(board
				.getWhiteClockLabel().getFont().getFontData()[0].getHeight()))
				+ topBottomNorthMargin + topBottomSouthMargin;

		// int bottomLabelHeight = topBottomLabelHeight;

		int boardWidthPixelsWest = width * BOARD_WIDTH_MARGIN_PERCENTAGES[WEST]
				/ 100;
		int boardWidthPixelsEast = width * BOARD_WIDTH_MARGIN_PERCENTAGES[EAST]
				/ 100;

		squareSize = (height - 2 * topBottomLabelHeight) / 8;

		while (width < squareSize * 10 + boardWidthPixelsWest
				+ boardWidthPixelsEast) {
			squareSize -= 2;
			hasSevereHeightProblem = true;
		}

		pieceJailSquareSize = squareSize;

		int topHeight = topBottomLabelHeight;

		int bottomControlStartY = topHeight + 8 * squareSize;

		boardTopLeft = new Point(boardWidthPixelsWest, topHeight);

		int rightControlStartX = boardTopLeft.x + boardWidthPixelsEast + 8
				* squareSize;

		int rightControlWidth = width - rightControlStartX;
		int oneHalfSquareSize = (int) (.5 * squareSize);

		gameDescriptionLabelRect = new Rectangle(rightControlStartX, topHeight,
				rightControlWidth, oneHalfSquareSize);

		currentPremovesLabelRect = new Rectangle(rightControlStartX, topHeight
				+ oneHalfSquareSize, rightControlWidth, oneHalfSquareSize);

		openingDescriptionLabelRect = new Rectangle(rightControlStartX,
				topHeight + 7 * squareSize, rightControlWidth,
				oneHalfSquareSize);

		statusLabelRect = new Rectangle(rightControlStartX, topHeight + 7
				* squareSize + oneHalfSquareSize, rightControlWidth,
				oneHalfSquareSize);

		topNameLabelRect = new Rectangle(boardWidthPixelsWest, 0,
				4 * squareSize, topBottomLabelHeight);
		topClockRect = new Rectangle(boardWidthPixelsWest + 4 * squareSize, 0,
				4 * squareSize, topBottomLabelHeight);
		topLagRect = new Rectangle(rightControlStartX, 0, rightControlWidth,
				topBottomLabelHeight);

		bottomNameLabelRect = new Rectangle(boardWidthPixelsWest,
				bottomControlStartY, 4 * squareSize, topBottomLabelHeight);
		bottomClockRect = new Rectangle(boardWidthPixelsWest + 4 * squareSize,
				bottomControlStartY, 4 * squareSize, topBottomLabelHeight);
		bottomLagRect = new Rectangle(rightControlStartX, bottomControlStartY,
				rightControlWidth, topBottomLabelHeight);

		topPieceJailTopLeft = new Point(rightControlStartX, topHeight
				+ squareSize);
		bottomPieceJailTopLeft = new Point(rightControlStartX, topHeight + 4
				* squareSize);
	}
}
