/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2000 CSELT S.p.A.
 
 GNU Lesser General Public License
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, 
 version 2.1 of the License. 
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

package jade.core.sam;

//#DOTNET_EXCLUDE_FILE

import java.io.Serial;
import java.io.Serializable;

/**
 * The class used by the System Activity Monitoring (SAM) Service to represent an aggregated 
 * measure i.e. a measure that is calculated aggregating several samples.
 * Actually the right name for this class should be AggregatedMeasure. The name 
 * AverageMeasure comes from the fact that computing the average of the samples is the
 * default way of aggregating them. However SUM can be used as an alternative aggregation. 
 */
public class AverageMeasure implements Serializable, Provider {
	@Serial
	private static final long serialVersionUID = 423475294834L;
	
	private double value = Double.NaN;
	private int nSamples;
	private double variance = 0.0;
	
	public AverageMeasure() {
	}
	
	public AverageMeasure(double value, int nSamples) {
		this.value = value;
		this.nSamples = nSamples;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getNSamples() {
		return nSamples;
	}
	public void setNSamples(int nSamples) {
		this.nSamples = nSamples;
	}
	public double getVariance() {
		return variance;
	}
	public void setVariance(double variance) {
		this.variance = variance;
	}
	
	public void update(AverageMeasure am) {
		update(am, SAMInfo.AVG_AGGREGATION);
	}
	
	public void update(AverageMeasure am, int aggregation) {
		if (Double.compare(value, Double.NaN) == 0) {
			// Local value is NaN --> Whatever it is get the other value 
			value = am.getValue();
			nSamples = am.getNSamples();
		} 
		else if (Double.compare(am.getValue(), Double.NaN) != 0) {
			// Both local and other values != NaN --> Do the actual update
			// NOTE: if other value == NaN --> just do nothing
			if (aggregation == SAMInfo.SUM_AGGREGATION) {
				// SUM Aggregation
				value = value + am.getValue();
				nSamples = nSamples + am.getNSamples();
			}
			else {
				// Default aggregation: AVG
				double totValue = value * nSamples + am.getValue() * am.getNSamples();
				int totSamples = nSamples + am.getNSamples();
				
				value = totSamples != 0 ? totValue / totSamples : 0.0;
				nSamples = totSamples;
			}
		}
		
		// FIXME: update variance
	}
}
