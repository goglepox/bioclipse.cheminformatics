/*******************************************************************************
 * Copyright (c) 2007-2009  Jonathan Alvarsson
 *               2008-2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.inchi.business;

import java.security.InvalidParameterException;

import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.inchi.InChI;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.sf.jniinchi.INCHI_RET;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

public class InChIManager implements IBioclipseManager {

    protected InChIGeneratorFactory factory;

    protected InChIGeneratorFactory getFactory() throws Exception {
        if (factory == null) {
            factory = new InChIGeneratorFactory();
        }
        return factory;
    }

    public String getManagerName() {
        return "inchi";
    }

    public void generate( IMolecule molecule, 
                          IReturner<InChI> returner,
                          IProgressMonitor monitor) 
                throws Exception {
    	monitor.beginTask("Calculating InChI", IProgressMonitor.UNKNOWN);
    	Object adapted = molecule.getAdapter(IAtomContainer.class);
        if (adapted != null) {
            IAtomContainer container = (IAtomContainer)adapted;
            InChIGenerator gen = getFactory().getInChIGenerator(container);
            INCHI_RET status = gen.getReturnStatus();
            if (status == INCHI_RET.OKAY ||
                status == INCHI_RET.WARNING) {
            	monitor.done();
                InChI inchi = new InChI();
                inchi.setValue(gen.getInchi());
                inchi.setKey(gen.getInchiKey());
                returner.completeReturn( inchi );
            } else {
                throw new InvalidParameterException(
                    "Error while generating InChI: " +
                    gen.getMessage()
                );
            }
        } else {
            throw new InvalidParameterException(
                "Given molecule must be a CDKMolecule"
            );
        }
    }

}
