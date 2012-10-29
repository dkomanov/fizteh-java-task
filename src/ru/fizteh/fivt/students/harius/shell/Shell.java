/*
 * Shell.java
 * Oct 6, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius;

import java.io.*;
import java.util.*;

public class Shell {
	private File current;

	public Shell() {

	}

	public void executeCommand(String multicmd) {
		StringTokenizer tok = new StringTokenizer(multicmd, ";");
		while (tok.hasMoreTokens()) {
			String cmd = tok.nextToken();
			executeSingleCommand(cmd);
		}
	}

	private void executeSingleCommand(String cmd) {
		
	}
}