package org.aroundthecode.pathfinder.server.controller.exception;

/**
 * Custom Exception to be rised upon failed Artifact storing
 * @author msacchetti
 *
 */
public class ArtifactSaveException extends Exception {


	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = -5998948208250133614L;

	/**
	 * Simple wrapper for external exception
	 * @param e exception
	 */
	public ArtifactSaveException(Exception e) {
		super(e);
	}
	
	
}
