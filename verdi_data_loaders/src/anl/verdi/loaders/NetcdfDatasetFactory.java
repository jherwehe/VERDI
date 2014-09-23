package anl.verdi.loaders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import simphony.util.messages.MessageCenter;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.conv.M3IOConvention;
import ucar.nc2.dataset.conv.WRFConvention;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import anl.verdi.core.VerdiConstants;
import anl.verdi.data.Dataset;

/**
 * Creates Dataset-s from netcdf files.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class NetcdfDatasetFactory {

	protected static final MessageCenter msgCenter = MessageCenter.getMessageCenter(NetcdfDatasetFactory.class);

	/**
	 * Creates a list of Datasets from the specified URL. The url
	 * should point to a netcdf file conforming to the CF convention.
	 *
	 * @param url a url that points to a netcdf file conforming to the
	 *            CF convention.
	 * @return a list of Datasets from the specified URL
	 */
	public List<Dataset> createCFDatasets(URL url) {
		GridDataset gridDataset = null;
		try {
			gridDataset = openNetcdfGridDataset(url);
			final NetcdfFile file = gridDataset.getNetcdfFile();
			final Attribute attribute = file.findGlobalAttribute("Conventions");
			final boolean isMine = attribute != null && attribute.getStringValue().startsWith( "CF-" );

			if ( ! isMine ) {
				throw new IOException("Loading non-CF file into CFDataset");
			}

			final List<Dataset> result = createDatasets(gridDataset, url, -1);
			return result;
		} catch (Exception io) {
			io.printStackTrace();
			msgCenter.error("Error reading netcdf file", io);
			try {
				if (gridDataset != null)
					gridDataset.close();
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Dataset>();
	}

	/**
	 * Creates a list of Datasets from the specified URL. The url
	 * should point to a netcdf file conforming to the Models-3 convention.
	 *
	 * @param url a url that points to a netcdf file conforming to the
	 *            Models-3 convention.
	 * @return a list of Datasets from the specified URL
	 */
	public List<Dataset> createModels3Datasets(URL url) {
		GridDataset gridDataset = null;
		try {
			gridDataset = openNetcdfGridDataset(url);
			if (!M3IOConvention.isMine(gridDataset.getNetcdfDataset())) {
				throw new IOException("Loading non-models3 file into Models3Dataset");
			}
			// if here then ok.
			return createDatasets(gridDataset, url, -1);
		} catch (Exception io) {
			io.printStackTrace();
			msgCenter.error("Error reading netcdf file", io);
			try {
				if (gridDataset != null)
					gridDataset.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Dataset>();
	}

	public Dataset createObsDataset(URL url) {
		NetcdfDataset dataset = null;
		try {
			String urlString = url.toExternalForm();
			if (url.getProtocol().equals("file")) {
				urlString = new URI(urlString).getPath();
			}
//			dataset = NetcdfDataset.openDataset(urlString, false, new CancelTask() {
				// 2014 require implementation of interface functions isCancel() and setProgress()
				// isCancel() previously implemented; no helpful information could be founc for setProgress()
				// documentation gives option of "null" for the CancelTask argument - see below
//				public boolean isCancel() {
//					return false;
//				}
//				public void setError(String msg) {}
//				@Override
//				public void setProgress(String arg0, int arg1) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			dataset = NetcdfDataset.openDataset(urlString, false, null);
			return new Models3ObsDataset(url, dataset);
		} catch (URISyntaxException e) {
			msgCenter.error("Error reading netcdf file", e);
		} catch (IOException e) {
			msgCenter.error("Error reading netcdf file", e);
		}

		return null;
	}

	/**
	 * Creates a list of Datasets from the specified URL. The url
	 * should point to a netcdf file conforming to the Models-3 convention.
	 *
	 * @param url a url that points to a netcdf file conforming to the
	 *            Models-3 convention.
	 * @return a list of Datasets from the specified URL
	 */
	public List<Dataset> createWRFDatasets(URL url) {
		GridDataset gridDataset = null;
		try {
			gridDataset = openNetcdfGridDataset(url); // JIZHEN-SHIFT
			if (!WRFConvention.isMine(gridDataset.getNetcdfDataset())) {
				throw new IOException("Loading non-models3 file into Models3Dataset");
			}
			// if here then ok.
			return createDatasets(gridDataset, url, VerdiConstants.NETCDF_CONV_ARW_WRF);
		} catch (Exception io) {
			msgCenter.error("Error reading netcdf file", io);
			try {
				if (gridDataset != null)
					gridDataset.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Dataset>();
	}

	// TODO: need to deal with extended GridDatasets that have DIMENSIONS besides X,Y,Z,Time, e.g. crop types, etc
	private List<Dataset> createDatasets(GridDataset gridDataset, URL url, int netcdfConv) {
		List<Dataset> sets = new ArrayList<Dataset>();
		// we need to maintain the order in which these are
		// created so we need the linked hashmap.
		Map<GridCoordSystem, List<GridDatatype>> map = new LinkedHashMap<GridCoordSystem, List<GridDatatype>>();
		for (GridDatatype grid : (List<GridDatatype>) gridDataset.getGrids()) {
			GridCoordSystem system = grid.getCoordinateSystem();
			
			List<GridDatatype> list = map.get(system);
			if (list == null) {
				list = new ArrayList<GridDatatype>();
				map.put(system, list);
			}
			list.add(grid);
		}
//		if ( 1==2 ) {
//			for ( GridCoordSystem system : map.keySet()) {
//				System.out.println("Coord system: " + system + ": ");
//				List<GridDatatype> grids = map.get(system);
//				for ( GridDatatype grid : grids ) {
//					System.out.println("\t"+grid);
//				}
//			}
//			
//		}
		if (map.values().size() == 1) {
			sets.add(new GridNetcdfDataset(url, map.values().iterator().next(), gridDataset));
		} else {
			int index = 1;
			for (List<GridDatatype> grids : map.values()) {
				GridNetcdfDataset dataset = new GridNetcdfDataset(url, grids, gridDataset, index++);
				dataset.setNetcdfConv(netcdfConv);
				sets.add(dataset);
			}
		}
		
		return sets;
	}

	private GridDataset openNetcdfGridDataset(URL url) throws URISyntaxException, IOException {
		String urlString = url.toExternalForm();
		
		if (url.getProtocol().equals("file")) {
			urlString = new URI(urlString).getPath();
		}
		
		validNetcdfFile( urlString);

		// return GridDataset.open(urlString); // NetCDF ENHANCE
		
		GridDataset gridDataset = GridDataset.open(urlString);
		
//		if ( 1 == 2 )
//			printGridDatasetInfo( gridDataset);

		return gridDataset;
	}   
	
	private NetcdfDataset openNetcdfDataset(URL url) throws URISyntaxException, IOException {
		String urlString = url.toExternalForm();
		
		if (url.getProtocol().equals("file")) {
			urlString = new URI(urlString).getPath();
		}
		
		validNetcdfFile( urlString);

		// return GridDataset.open(urlString); // NetCDF ENHANCE
		
		NetcdfDataset netcdfDataset = NetcdfDataset.openDataset(urlString);
		
//		if ( 1 == 2 )
//			printNetcdfDatasetInfo( netcdfDataset);

		return netcdfDataset;
	} 
	
	private void validNetcdfFile(String urlString) throws IOException {
		if ( urlString == null) {
			throw new IOException("Invalid netcdf file: url is null.");
		}
		
		NetcdfFile file = null;
		try {
			file = NetcdfFile.open( urlString );
		} catch (IOException e) {
			throw new IOException("Invalid netcdf file: " + e.getMessage());
		} finally {
			if ( file != null)
				file.close();
		}
	}
	
	private void printNetcdfDatasetInfo( NetcdfDataset netcdfDataset) {
		
		System.out.println("Detailed information: " + netcdfDataset.getDetailInfo());
		System.out.println("File type description: " + netcdfDataset.getFileTypeDescription());
		System.out.println("Coordinate Systems: " + netcdfDataset.getCoordinateSystems().toString());
		List<Variable> variables = netcdfDataset.getVariables();

		List<Dimension> dims = netcdfDataset.getDimensions();
		System.out.println("# of dimensions: " + dims.size());
		Iterator<Dimension> dimIt = dims.iterator();
		while( dimIt.hasNext()) {
			Dimension dim = dimIt.next();	
			System.out.println("Dim: " + dim);				
		}
		dimIt = null;
		
		System.out.println("# of vars: " + variables.size());
		List<Variable> netcdfVars = netcdfDataset.getVariables();
		Iterator<Variable> varIt = netcdfVars.iterator();
		while( varIt.hasNext()) {
			Variable var = varIt.next();	
			System.out.println("Netcdf Var: " + var);
		}
		varIt = null;
	}
	
	private void printGridDatasetInfo( GridDataset gridDataset) {
		
		System.out.println("Desc: " + gridDataset.getDescription());
		System.out.println("Info: " + gridDataset.getDetailInfo());
		System.out.println("Feature type: " + gridDataset.getFeatureType().toString());
		
		NetcdfDataset netcdfDataset = gridDataset.getNetcdfDataset();
		List<Dimension> dims = netcdfDataset.getDimensions();
		System.out.println("# of dimensions: " + dims.size());
		Iterator<Dimension> dimIt = dims.iterator();
		while( dimIt.hasNext()) {
			Dimension dim = dimIt.next();	
			System.out.println("Dim: " + dim);
		}
		dimIt = null;
		
		List<VariableSimpleIF> variables = gridDataset.getDataVariables();
		System.out.println("Vars got from GridDataset: ");
		System.out.println("# of vars: " + variables.size());
		Iterator<VariableSimpleIF> it = variables.iterator();
		while ( it.hasNext()) {
			VariableSimpleIF sVar = it.next();	
			System.out.println("Var: " + sVar);
		}
		it = null;

		System.out.println("Vars got from NetcdfDataset: ");
		List<Variable> netcdfVars = netcdfDataset.getVariables();
		Iterator<Variable> varIt = netcdfVars.iterator();
		while( varIt.hasNext()) {
			Variable var = varIt.next();	
			System.out.println("Netcdf Var: " + var);
		}
		varIt = null;
	}
}