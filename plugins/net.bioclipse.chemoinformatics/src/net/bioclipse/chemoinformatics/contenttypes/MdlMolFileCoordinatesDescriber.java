/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     Stefan Kuhn
 *     Carl M�sak
 *     
 ******************************************************************************/
package net.bioclipse.chemoinformatics.contenttypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;


import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("restriction")
public class MdlMolFileCoordinatesDescriber extends TextContentDescriber 
										 implements IExecutableExtension {
	
	private static final String ELEMENT_TO_FIND = "type"; //$NON-NLS-1$

	private String elementToFind = null;


	/**
	 * Read 100 chars, search for x2 or x2
	 */
	public int describe(InputStream contents, IContentDescription description) 
															throws IOException {

		InputStreamReader r=new InputStreamReader(contents);
		return describe(r, description);
	}


	/**
	 * Read 100 chars, search for x2 or x2
	 */
	public int describe(Reader contents, IContentDescription description) {

		BufferedReader br=new BufferedReader(contents);
		
		try {
			return deduceCoordinates(br);
		} catch (Exception e) {
			System.out.println("Problem trying to detect coordinates: "+e.getMessage());
			return INVALID;
		}
	}

	private int deduceCoordinates(BufferedReader br) throws IOException {

		//Ignore first 3 lines
		//masak,jonalv,egonw: I deliberately did not use a loop to annoy you!
		br.readLine();
		br.readLine();
		br.readLine();

		//Read no atoms and set max to 10
		String line=br.readLine();
        int atoms = Integer.parseInt(line.substring(0, 3).trim());
        if (atoms>10) atoms=10;

		//check for 3D coords for max 10 atoms
		/*
	    3.2499   -0.0709   -0.2792 C   0  0  0  0  0
	    1.8908    0.6518   -0.4095 C   0  0  0  0  0
	    0.7354   -0.2732    0.0428 C   0  0  0  0  0
	   -0.6293    0.4199   -0.2145 C   0  0  0  0  0
	   */
		double totalX=0;
        double totalY=0;
        double totalZ=0;
		for (int i=0; i<atoms;i++){
			line=br.readLine();
            double x = Double.parseDouble(line.substring(0, 10).trim());
            totalX += Math.abs(x); // *all* values should be zero, not just the sum
            double y = Double.parseDouble(line.substring(10, 20).trim());
            totalY += Math.abs(y); // *all* values should be zero, not just the sum
            double z = Double.parseDouble(line.substring(20, 30).trim());
            totalZ += Math.abs(z); // *all* values should be zero, not just the sum
		}
		
		if (totalZ==0 && totalX!=0 && totalY!=0 &&
		    elementToFind.equalsIgnoreCase("2D")){
			return VALID;
		}

		if (totalZ!=0 && totalX!=0 && totalY!=0 &&
		    elementToFind.equalsIgnoreCase("3D")){
			return VALID;
		}

        if (totalZ==0 && totalX==0 && totalY==0 &&
            elementToFind.equalsIgnoreCase("0D")){
            return VALID;
        }

		//Else, invalid (or indeterminate?)
		return INVALID;

		
	}


	/**
	 * Store parameters
	 */
	@SuppressWarnings("unchecked")
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data instanceof String)
			elementToFind = (String) data;
		else if (data instanceof Hashtable) {
			Hashtable parameters = (Hashtable) data;
 			elementToFind = (String) parameters.get(ELEMENT_TO_FIND);
		}
		if (elementToFind == null) {
			String message = NLS.bind(ContentMessages.content_badInitializationData, MdlMolFileCoordinatesDescriber.class.getName());
			throw new CoreException(new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, null));
		}
	}
}
