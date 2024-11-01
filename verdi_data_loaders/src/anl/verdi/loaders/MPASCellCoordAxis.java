/**
 * CoordAxis that can be built from net cdf specific variables.
 *
 * @author Tony Howard
 * @version $Revision$ $Date$
 */

package anl.verdi.loaders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;		// 2014
import org.apache.logging.log4j.Logger;			// 2014 replacing System.out.println with logger messages
import org.unitsofmeasurement.unit.Unit;

import ucar.nc2.Variable;
import anl.verdi.data.AxisType;
import anl.verdi.data.CoordAxis;
import anl.verdi.data.MPASCellAxis;
import anl.verdi.data.Range;
import anl.verdi.util.VUnits;

@SuppressWarnings("rawtypes")
public class MPASCellCoordAxis implements CoordAxis, MPASCellAxis {

	static final Logger Logger = LogManager.getLogger(MPASCellCoordAxis.class.getName());
	private Range range;
	private AxisType type;
	private Unit unit;
	protected String name, description;
	protected int length;
	CoordAxis xAxis;
	CoordAxis yAxis;
	MPASDataset dataset;
	
	public MPASCellCoordAxis(CoordAxis xAxis, CoordAxis yAxis, int length, String name, String description, MPASDataset set) {
		this.name = name;
		this.description = description;
		this.unit = VUnits.MISSING_UNIT;
		this.type = AxisType.CELL_AXIS;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.range = new Range(0, length);
		this.length = length;
		this.dataset = set;
		Logger.debug("in CSVCoordAxis constructor, unit = " + this.unit);
	}

	/**
	 * Gets the value at the specified index.
	 *
	 * @return the value at the specified index.
	 */
	public double getValue(int index) {
		
		return index;
	}

	/**
	 * Gets the unit of measurement for this coordinate axis.
	 *
	 * @return the unit of measurement for this coordinate axis.
	 */
	public Unit getUnits() {
		Logger.debug("in CSVCoordAxis.getUnits, unit = " + unit);
		return unit;
	}


	/**
	 * Gets whether or not the this axis is compatible with
	 * the specified axis. Two axes are compatible if they
	 * can be used together in the same formula.
	 *
	 * @param axis the axis to check for compatibility
	 * @return true if this axis is compatibility with the other axis,
	 *         Otherwise false.
	 */
	public boolean isCompatible(CoordAxis axis) {
		// identity test
		if (axis == this) return true;
		long extent = axis.getRange().getExtent();
		if (axis.getAxisType().equals(this.getAxisType()) &&
						extent == this.getRange().getExtent()) {
			for (int i = 0; i < extent; i++) {
				if (getValue(i) != axis.getValue(i)) return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the description of this CoordAxis.
	 *
	 * @return the description of this CoordAxis.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the name of this CoordAxis.
	 *
	 * @return the name of this CoordAxis.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the range of this axis.
	 *
	 * @return the range of this axis.
	 */
	public Range getRange() {
		return range;
	}


	/**
	 * Gets the type of this axis.
	 *
	 * @return the type of this axis.
	 */
	public AxisType getAxisType() {
		return type;
	}
	
	public CoordAxis getXAxis() {
		return xAxis;
	}
	
	public CoordAxis getYAxis() {
		return yAxis;
	}
	
	public CoordAxis getZAxis(String variable) {
		List<CoordAxis> axisList = dataset.getCoordAxes().getAxes();
		Variable var = dataset.getVariableDS(dataset.getVariable(variable));
		Set<String> dimensions = new HashSet<String>();
		dimensions.addAll(Arrays.asList(var.getDimensionsString().split("\\s+")));
		for (CoordAxis axis : axisList) {
			if (axis.getAxisType().equals(AxisType.LAYER) && dimensions.contains(axis.getName()))
				return axis;
		}
		return dataset.getDefaultZAxis();
	}
	
	
}
