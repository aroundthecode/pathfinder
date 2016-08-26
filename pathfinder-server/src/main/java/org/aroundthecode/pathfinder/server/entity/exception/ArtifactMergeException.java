package org.aroundthecode.pathfinder.server.entity.exception;

/**
 * Custom Exception to be rised upon failed Artifact Merge
 * @author msacchetti
 *
 */
public class ArtifactMergeException extends Exception {

	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = 5024977884267387716L;

	/**
	 * Simple wrapper for external exception
	 * @param e exception
	 */
	public ArtifactMergeException(Exception e) {
		super(e);
	}

	/**
	 * Simple wrapper for message handling
	 * @param message exception reason message
	 */
	public ArtifactMergeException(String message) {
		super(message);
	}
	
	

}
