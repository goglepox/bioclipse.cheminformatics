package net.bioclipse.cdk.jchempaint.undoredo;

import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.controller.undoredo.MergeMoleculesEdit;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/*******************************************************************************
 * Copyright (c) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
public class SWTMergeMoleculesEdit extends MergeMoleculesEdit implements
        IUndoableOperation {

    IUndoContext context;

    public SWTMergeMoleculesEdit(IAtom deletedAtom, IAtomContainer containerWhereAtomWasIn, List<IBond> deletedBonds, Map<IBond, Integer> bondsWithReplacedAtom, Vector2d offset, IAtom atomwhichwasmoved, String type, IChemModelRelay c2dm,  IUndoContext context) {
        super(deletedAtom,containerWhereAtomWasIn,deletedBonds,bondsWithReplacedAtom,offset,atomwhichwasmoved,type,c2dm);
        this.context = context;
    }
    public IStatus redo( IProgressMonitor monitor, IAdaptable info )
                                                                    throws ExecutionException {

        super.redo();
        return Status.OK_STATUS;
    }

    public void removeContext( IUndoContext context ) {

        // TODO Auto-generated method stub

    }

    public IStatus undo( IProgressMonitor monitor, IAdaptable info )
                                                                    throws ExecutionException {

        super.undo();
        return Status.OK_STATUS;
    }

    public IStatus execute( IProgressMonitor monitor, IAdaptable info )
                                                                       throws ExecutionException {

        // TODO Auto-generated method stub
        return Status.OK_STATUS;
    }

    public void addContext( IUndoContext context ) {

    }

    public boolean canExecute() {

        // TODO Auto-generated method stub
        return false;
    }

    public void dispose() {

        // TODO Auto-generated method stub

    }

    public IUndoContext[] getContexts() {

        return new IUndoContext[] { this.context };
    }

    public String getLabel() {

        return super.getPresentationName();
    }

    public boolean hasContext( IUndoContext context ) {

        // TODO Auto-generated method stub
        return context.matches( this.context );
    }

}
