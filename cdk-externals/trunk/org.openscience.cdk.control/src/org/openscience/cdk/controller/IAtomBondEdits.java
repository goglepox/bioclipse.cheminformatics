/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.controller;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;

/**
 *  Interface for edit methods currently used by edit modules but should
 *  be replaced by IEdit commands.
 * @author Arvid
 * @cdk.module control
 * @deprecated Use <code>IEdits</code> instead
 */
@Deprecated
public interface IAtomBondEdits {
    /* Editing actions for atoms */
    public IAtomContainer removeAtom(IAtom atom);
    public IAtomContainer removeAtomWithoutUndo(IAtom atom);
    public IAtom addAtom(String element, Point2d worldcoord);
    public IAtom addAtomWithoutUndo(String element, Point2d worldcoord);
    public IAtom addAtom(String element, IAtom atom);
    public IAtom addAtomWithoutUndo(String element, IAtom atom);
    public void moveToWithoutUndo(IAtom atom, Point2d point);
    public void moveTo(IAtom atom, Point2d point);
    public void setSymbol(IAtom atom, String symbol);
    public void setCharge(IAtom atom, int charge);
    public void setMassNumber(IAtom atom, int charge);
    public void setHydrogenCount(IAtom atom, int intValue);
    public void replaceAtom(IAtom atomnew, IAtom atomold);
    public void addSingleElectron(IAtom atom);
    public void updateAtoms(IAtomContainer container, Iterable<IAtom> atoms);
    public void updateAtom(IAtom atom);
    public void mergeMolecules(Vector2d movedDistance);

    /* Editing actions for bonds */
    public IBond addBond(IAtom fromAtom, IAtom toAtom);
    public void removeBondWithoutUndo(IBond bond);
    public void removeBond(IBond bond);
    public void moveToWithoutUndo(IBond bond, Point2d point);
    public void moveTo(IBond bond, Point2d point);
    public void setOrder(IBond bond, IBond.Order order);
    public void setWedgeType(IBond bond, IBond.Stereo type);
    public void addNewBond(Point2d worldCoordinate);
    public void cycleBondValence(IBond bond);
    public void makeBondStereo(IBond bond, IBond.Stereo desiredDirection);
    public IBond makeNewStereoBond(IAtom atom, IBond.Stereo desiredDirection);

    /* Editing actions for rings*/
    public IRing addRing(int size, Point2d worldcoord);
    public IRing addRing(IAtom atom, int size);
    public IRing addPhenyl(IAtom atom);
    public IRing addPhenyl(Point2d worldcoord);
    public IRing addRing(IBond bond, int size);
    public IRing addPhenyl(IBond bond);
}
