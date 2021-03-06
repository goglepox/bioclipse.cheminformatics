/*******************************************************************************
 * Copyright (c) 2009 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 *
 * Contributors:
 *     Arvid Berg <goglepox@users.sourceforge.net>
 *
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.generators;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import net.bioclipse.cdk.jchempaint.Activator;
import net.bioclipse.cdk.jchempaint.business.IJChemPaintManager;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.PathElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;


/**
 * @author arvid
 *
 */
public class ClosestToCenterOfMassGenerator implements IGenerator {

    private static final Color STAR_COLOR = Color.ORANGE;
    /**
     *
     */
    public ClosestToCenterOfMassGenerator() {
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.renderer.generators.IGenerator#generate(org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.renderer.RendererModel)
     */
    public IRenderingElement generate( IAtomContainer ac, RendererModel model ) {
        IJChemPaintManager jcp = Activator.getDefault().getJavaManager();
        Point2d p2=GeometryTools.get2DCentreOfMass( ac);
        if(p2== null) return new ElementGroup();
        IAtom atom = jcp.getClosestAtom(p2);
        if(atom==null || atom.getPoint2d()==null) return new ElementGroup();
        return generateStar( atom.getPoint2d(),
                             model.getHighlightDistance()*3/model.getScale(),
                             model.getBondWidth()/model.getScale());
    }

    protected IRenderingElement generateStar( Point2d center,
                                              double radius,
                                              double width) {
//        if(true)
//            return new OvalElement(center.x,center.y,radius,true,STAR_COLOR);
        ElementGroup star = new ElementGroup();
        Matrix3d m3 = new Matrix3d();
        m3.rotZ(Math.toRadians( 360d/5 ) );
        Vector3d v1 = new Vector3d(0,-1,0);
        v1.scale( radius );

        Vector2d[] vecs = new Vector2d[5];
        for(int i=0;i<vecs.length;i++) {
            vecs[i]=new Vector2d(v1.x,v1.y);
            m3.transform( v1 );
        }

        Point2d p1 = new Point2d();
        Point2d p2 = new Point2d();

        for(int i =0;i<vecs.length;i++) {

            p1.add( center, vecs[i] );
            p2.add( center, vecs[(i+2)%vecs.length] );
            star.add( createLine(p1,p2,width) );
        }

        return star;
    }

    protected IRenderingElement generateFilledStar( Point2d center,
                                                    double radius,
                                                    int points,
                                                    double width) {
        Matrix3d m3 = new Matrix3d();
        m3.rotZ( 2*Math.PI/(points*2) );
        Vector3d v1 = new Vector3d(0,-1,0);
        Vector3d v2 = new Vector3d(0,-1,0);
        v1.scale( radius );
        v2.scale( radius/2 );

        Vector2d[] vecs = new Vector2d[points*2];
        for(int i =0;i<vecs.length;i+=2) {
            vecs[i] = new Vector2d(v1.x,v1.y);
            m3.transform( v1 );
            vecs[i+1] = new Vector2d(v1.x,v1.y);
            vecs[i+1].scale( .4 );
            m3.transform( v1 );
        }

        Point2d first= null;
        Point2d p1 = new Point2d();
        ArrayList<Point2d> pts = new ArrayList<Point2d>();
        for(int i =0;i<vecs.length;i++) {

            p1.add( center, vecs[i] );
            if(first == null) first = new Point2d(p1);
            pts.add( new Point2d(p1) );
        }
        pts.add(first);
        return new PathElement(pts,Color.ORANGE);
    }

    private LineElement createLine(Point2d p1,Point2d p2,double width) {
        return new LineElement(p1.x,p1.y,p2.x,p2.y,width,STAR_COLOR);
    }

    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}
