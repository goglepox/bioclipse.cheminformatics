/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 *
 * Contributors:
 *        Arvid Berg <goglepox@users.sf.net>
 ******************************************************************************/
package net.bioclipse.cdk.ui.sdfeditor.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.business.MappingEditorModel;
import net.bioclipse.cdk.ui.sdfeditor.business.SDFIndexEditorModel;
import net.bioclipse.cdk.ui.views.IFileMoleculesEditorModel;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.cdk.ui.views.ISortable;
import net.bioclipse.cdk.ui.views.ISortable.SortProperty;
import net.bioclipse.core.domain.IMolecule.Property;
import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.model.DefaultNatTableModel;
import net.sourceforge.nattable.model.INatTableModel;
import net.sourceforge.nattable.sorting.ISortingDirectionChangeListener;
import net.sourceforge.nattable.sorting.SortingDirection;
import net.sourceforge.nattable.sorting.SortingDirection.DirectionEnum;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author arvid
 */
public class MoleculeTableContentProvider implements
        ILazyContentProvider, IDataProvider ,IMoleculeTableColumnHandler
        {

    Logger logger = Logger.getLogger( MoleculeTableContentProvider.class );

    public static int READ_AHEAD = 100;
    static final int NUMBER_OF_PROPERTIES =10;

    private MoleculeTableViewer viewer;
    IMoleculesEditorModel   model       = null;
    List<Object> properties = new ArrayList<Object>(NUMBER_OF_PROPERTIES);
//    Collection<Object> availableProperties = new HashSet<Object>();

    MoleculesEditorLabelProvider melp = new MoleculesEditorLabelProvider(
                                    MoleculeTableViewer.STRUCTURE_COLUMN_WIDTH);

    private ISortingDirectionChangeListener sortDirListener;

    public MoleculesEditorLabelProvider getLabelProvider() {
        return melp;
    }

    public List<Object> getProperties() {

        return new ArrayList<Object>(properties);
    }

    public Collection<Object> getAvailableProperties() {
        return model.getAvailableProperties();
    }

    public void removeColumn(Object key) {
        if(model instanceof MappingEditorModel) {
            model = ((MappingEditorModel)model).getModel();
        }
        if(model instanceof SDFIndexEditorModel) {
            SDFIndexEditorModel sdModel = (SDFIndexEditorModel)model;
            sdModel.removePropertyKey( key );
            properties.retainAll( sdModel.getAvailableProperties() );
            updateHeaders();
        }
    }

    private void setModel(IMoleculesEditorModel model) {
        this.model = model;

        if(viewer !=null)
            viewer.refresh();
    }

    public ICDKMolecule getMoleculeAt( int index ) {
        ICDKMolecule molecule = null;
        IMoleculesEditorModel lModel;
        synchronized ( this ) {
            lModel = model;
        }
        if ( lModel != null ) {
            molecule = lModel.getMoleculeAt( index );
        }

        return molecule;
    }

    public void inputChanged( final Viewer viewer, Object oldInput, Object newInput ) {

        if(newInput == null) {
            return;
        }

        if(viewer != this.viewer) {
            this.viewer = (MoleculeTableViewer)viewer;
        }
        if(newInput instanceof SDFIndexEditorModel) {
            setModel(new MappingEditorModel( (IFileMoleculesEditorModel )newInput));
        }else
        if(newInput instanceof IMoleculesEditorModel)
            setModel((IMoleculesEditorModel) newInput);
        else if(newInput instanceof IAdaptable){
            setModel( (IMoleculesEditorModel)
                      ((IAdaptable)newInput).getAdapter(
                                                 IMoleculesEditorModel.class ));
        }
        if(this.viewer != null)
            updateSize( (model!=null?model.getNumberOfMolecules():0) );

        // fill properties with elements from availableProperties
        properties.clear();
        Iterator<Object> iter = getAvailableProperties().iterator();
        for(int i=0;i<NUMBER_OF_PROPERTIES;i++) {
            if(iter.hasNext())
                properties.add(iter.next());
        }
        updateHeaders();

        NatTable table = this.viewer.table;
        if ( model instanceof ISortable ) {
            table.removeSortingDirectionChangeListener( sortDirListener );
            final ISortable sortModel = (ISortable) model;

            ISortingDirectionChangeListener listener
                                    = new ISortingDirectionChangeListener() {

          public void sortingDirectionChanged( SortingDirection[] directions ) {

                    List<SortProperty<?>> sortOrder;
                    if ( directions.length <= 0 ) {
                        sortOrder = Collections.emptyList();
                        sortModel.setSortingProperties( sortOrder );
                        return;
                    }
                    sortOrder = new ArrayList<SortProperty<?>>();
                    for(SortingDirection sDir:directions) {
                        if(sDir.getColumn() == 0) continue;
                        sortOrder.add( new SortProperty<Object>(
                                        properties.get(sDir.getColumn()-1),
                                        sDir.getDirection()==DirectionEnum.UP
                                         ?ISortable.SortDirection.Ascending
                                         :ISortable.SortDirection.Descending) );
                    }
                    sortModel.setSortingProperties( sortOrder );
               }
            };
            setSortListener( listener );
        }else {
            setSortListener( null );
        }

    }

    private void setSortListener( ISortingDirectionChangeListener listener) {
        NatTable table = this.viewer.table;
        if(listener==null)
            table.removeSortingDirectionChangeListener( sortDirListener );
        else
            table.addSortingDirectionChangeListener( listener );
        sortDirListener = listener;
        INatTableModel mod = table.getNatTableModel();
        if(mod instanceof DefaultNatTableModel)
            ((DefaultNatTableModel)mod)
                .setSortingEnabled( listener==null?false:true );
    }

    public void updateHeaders() {

        viewer.refresh();
    }

    private NatTable getCompositeTable(Viewer viewer) {

        return (NatTable)viewer.getControl();
    }

    enum FileType {
        SDF,SMI
    }

    private void updateSize(int size) {
        getCompositeTable( viewer ).redraw();
    }


    public void updateElement( int index ) {

    }

    public void dispose() {

    }

    public void setVisibleProperties( List<Object> visibleProperties ) {
        properties.clear();
        properties.addAll( visibleProperties );
        updateHeaders();
    }

    public int getColumnCount() {
        return properties.size()+1;
    }

    public int getRowCount() {
        IMoleculesEditorModel tModel = model;
        if(tModel != null)
            return tModel.getNumberOfMolecules();
        return 0;
    }

    public Object getValue( int row, int col ) {
        if(row>=getNumberOfMolecules()) return "";
        IMoleculesEditorModel tModel = model;
        ICDKMolecule molecule =  (ICDKMolecule) tModel.getMoleculeAt( row );
        if(col == 0) {
            return molecule;
        }
        int i = col;
        if( molecule!=null && properties != null && i<properties.size()+1) {
            Object v = molecule.getProperty( (String)properties.get( i-1 ),
                                                 Property.USE_CACHED );
            if(v != null) {
                return v;
//            if(model instanceof SDFIndexEditorModel) {
//                // FIXME a general interface to access properties
//                return ((SDFIndexEditorModel) model).getPropertyFor(
//                                             row, (String)properties.get(i-1) );
            }else {
            Object value = molecule.getAtomContainer()
            .getProperty( properties.get(i-1));
            return  value!=null?value.toString():"";
            }
        } else
            return "";
    }

    public int getNumberOfMolecules() {
        return getRowCount();
    }
}
