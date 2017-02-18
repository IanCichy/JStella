package jstella.learning;

import java.awt.event.KeyEvent;

public interface JSIGame {

	/**
	 * ----getScore----
	 * Gets the current score in game
	 * @return int: returns an integer that represents number of points earned in any given game
	 */
	public int getScore();
	
	/**
	 * ----getLives----
	 * Gets the current number of lives
	 * @return int: returns an integer that represents the amount of lives player0 has in game
	 *      if this feature is present, else 0
	 */
	public int getLives();
	
	/**
	 * ----getLevel----
	 * Gets the current level in game
	 * @return int: returns an integer that represents the level in game 
	 *      if this feature is present, else 0
	 */
	public int getLevel();
	
	/**
	 * ----getValidActions----
	 * Gets the valid actions for any given game
	 * @return int[]: returns an integer array that represents actions that can be taken in a game
	 *      These combinations range from 1-3 commands at once from the options:
	 *      NULL, UP, LEFT, DOWN, RIGHT, SPACE
	 *      18 Total Actions
	 */
	default int[][] getValidActions(){
		return new int[][] {
			{KeyEvent.VK_UP},
			{KeyEvent.VK_DOWN},
			{KeyEvent.VK_LEFT},
			{KeyEvent.VK_RIGHT},
			{KeyEvent.VK_SPACE},
			{0},
			{KeyEvent.VK_UP, KeyEvent.VK_LEFT},
			{KeyEvent.VK_UP, KeyEvent.VK_RIGHT},
			{KeyEvent.VK_UP, KeyEvent.VK_SPACE},
			{KeyEvent.VK_DOWN, KeyEvent.VK_LEFT},
			{KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT},
			{KeyEvent.VK_DOWN, KeyEvent.VK_SPACE},
			{KeyEvent.VK_LEFT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE}
			};
	}
}
