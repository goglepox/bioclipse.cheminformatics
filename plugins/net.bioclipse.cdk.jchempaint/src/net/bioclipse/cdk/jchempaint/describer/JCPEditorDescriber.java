/*******************************************************************************
  * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.describer;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.ui.business.describer.IBioObjectDescriber;


public class JCPEditorDescriber implements IBioObjectDescriber {

    ICDKManager cdk;

    public JCPEditorDescriber() {

        cdk=Activator.getDefault().getJavaCDKManager();
    }

    public String getPreferredEditorID( IBioObject object ) {
        if ( object instanceof ICDKMolecule ) {
            ICDKMolecule cdkmol = (ICDKMolecule) object;
            try {
                if (!cdk.has3d( cdkmol )){
                    return "net.bioclipse.cdk.ui.editors.jchempaint";
                }
            } catch ( BioclipseException e ) {
            }
        }

        return null;
    }

}
