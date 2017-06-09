package plugin_suite.charts;

import com.atlassian.jira.charts.jfreechart.ChartGenerator;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.charts.jfreechart.util.ChartUtil;
import com.atlassian.jira.util.I18nHelper;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generator for charts that trend data over time.
 */
public class TimeTrendingChartGenerator implements ChartGenerator {
	private static final Logger log = LoggerFactory.getLogger(TimeTrendingChartGenerator.class);
	private final String title;
	private final XYDataset dataset;
	private final String timeAxisLabel;	// Typically the x-axis if orientation is vertical
	private final String valueAxisLabel;	// Typically the y-axis if orientation is vertical
	private int width;
	private int height;
	private int dateInterval;
	private int dataRange;
	private Date lowerBound;
	private Date upperBound;
	private I18nHelper i18nHelper;
	
	private boolean useLegend;
	private boolean generateTooltips;
	private boolean generateUrls;
	private Color backgroundPaint;
	
	// Static variable for the class to use
	private static Calendar cal = new GregorianCalendar();
	
	/**
	 * Sets the default parameters for any charts generator by this class.
	 */
	public TimeTrendingChartGenerator(String title, XYDataset dataset, String timeAxisLabel, 
			String valueAxisLabel, int width, int height, int dateInterval, int dataRange, Date upperBound, 
			I18nHelper i18nHelper) {
		// Parameters passed by constructor
		this.title = title;
		this.dataset = dataset;
		this.timeAxisLabel = timeAxisLabel;
		this.valueAxisLabel = valueAxisLabel;
		this.width = width;
		this.height = height;
		this.dateInterval = dateInterval;
		this.dataRange = dataRange;
		this.lowerBound = getLowerBound(this.dataRange, this.dateInterval, upperBound);
		this.upperBound = upperBound;
		this.i18nHelper = i18nHelper;
		
		// Parameters set by default
		useLegend = false;
		generateTooltips = true;
		generateUrls = true;
		backgroundPaint = Color.WHITE;
	}
	
	/**
	 * Generates time trending chart and returns the helper object for it.
	 * 
     * @return Chart Helper object that will help render the chart within Jira.
	 */
	@SuppressWarnings("deprecation")
	public ChartHelper generateChart() {
		// Produces JFreeChart object and wraps it using Atlassian's Chart object
    	JFreeChart chart = createLineChart();
    	
    	// Sets chart defaults
    	ChartUtil.setDefaults(chart, i18nHelper);
    	
    	// Creates helper for chart and uses it to generate the chart
    	ChartHelper chartHelper = new ChartHelper(chart);
    	try {
    		try {
    			chartHelper.generateInline(width, height);
    		} catch (NoSuchMethodError e) {
    			chartHelper.generate(width, height);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
		return chartHelper;
	}
    
    /**
     * Creates a customized line chart based on given data and sets its parameters accordingly.
     * 
     * @param firstDate - First date on the axis of the line chart.
     * @param lastDate - Last date on the axis of the line chart.
     */
    private JFreeChart createLineChart() {
    	// Creates initial line chart
    	JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, 
    			dataset, useLegend, generateTooltips, generateUrls);
    	
    	// Sets background of the chart
    	chart.setBackgroundPaint(backgroundPaint);
    	
    	// Sets the x axis formatting
    	final XYPlot plot = chart.getXYPlot();
    	final DateAxis axis = (DateAxis) plot.getDomainAxis();
    	IntervalDateAxis newAxis = new IntervalDateAxis(axis.getLabel(), lowerBound, dateInterval);
    	newAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
    	newAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, dateInterval));
    	newAxis.setVerticalTickLabels(true);
    	newAxis.setTickMarksVisible(true);
    	newAxis.setMinorTickMarksVisible(true);
    	
    	// Set formatting options of new axis based on original axis
    	newAxis.setTimeZone(axis.getTimeZone());
    	newAxis.setLabelPaint(axis.getLabelPaint());
    	newAxis.setLabelInsets(axis.getLabelInsets());
    	newAxis.setLabelPaint(axis.getLabelPaint());
    	newAxis.setTickLabelFont(axis.getTickLabelFont());
    	newAxis.setTickLabelInsets(axis.getTickLabelInsets());
    	newAxis.setTickLabelPaint(axis.getTickLabelPaint());
    	newAxis.setTickMarkPosition(axis.getTickMarkPosition());
    	newAxis.setFixedDimension(axis.getFixedDimension());
    	
    	
    	// Sets data range (offsets by 1, since min and max dates aren't inclusive)
    	newAxis.setMinimumDate(getOffsetDate(lowerBound, -1));
    	newAxis.setMaximumDate(getOffsetDate(upperBound, 1));
    	
    	// Sets chart axis based on customized date axis
    	plot.setDomainAxis(newAxis);
    	
    	return chart;
    }
    
    /**
     * Gets the lower bound of the data range, determined by the upper bound passed, 
     * subtracted by the dataRange stored by the generator, which represents the 
     * number of days within the data range.
     * 
     * The final offset will always be a multiple of the date interval, so if the 
     * initial data range is not a multiple of the data interval, an offset is
     * calculated and applied to it accordingly to make it so. If this isn't done,
     * the chart using this lower bound will not show today's date as the upper
     * bound due to the intervals between each date on the x-axis.
     * 
     * @param date - Upper bound for the data range.
     * @return Date object representing the lower bound of the data range.
     */
    public static Date getLowerBound(int dataRange, int dateInterval, Date upperBound) {
    	long offsetFromDataRange = dataRange % dateInterval != 0? 
    			dateInterval - (dataRange % dateInterval): 0;
    	long finalOffset = (dataRange + offsetFromDataRange) * -1;
    	return getOffsetDate(upperBound, finalOffset);
    }
    
    /**
	 * Removes time component from the given date and returns result.
	 * 
	 * @param date - Date to remove time from.
	 * @return Date object without time components.
	 */
	public static Date removeDateTime(Date date) {
		cal.setTime(date);
	    cal.clear(Calendar.MILLISECOND);
	    cal.clear(Calendar.SECOND);
	    cal.clear(Calendar.MINUTE);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    return cal.getTime();
	}
    
    /**
     * Gets the resulting date after shifting the given date by the offset
     * parameter (the number of days to shift the original date by). It calculates 
     * final date by converting it into milliseconds since 1/1/1970, then adding 
     * the offset converted into milliseconds, and then passing the result into a 
     * new Date object.
     * 
     * @param originalDate - Original date to serve as the starting shift point.
     * @param offset - Offset (number of days) applied to the original date. Can be negative.
     * @return Shifted date with offset applied to original.
     */
    public static Date getOffsetDate(Date originalDate, long offset) {
    	return new Date(originalDate.getTime() + offset*1000*60*60*24);
    }
    
    /**
     * Customized Date Axis class that overrides the two functions responsible 
     * for setting tick marks, so that the minimum and maximum dates are always 
     * visible on the date axis as end points.
     */
    private class IntervalDateAxis extends DateAxis {
		private static final long serialVersionUID = 1L;
		private Date firstDate;
    	private int interval;
    	
    	private IntervalDateAxis(String label, Date firstDate, int interval) {
    	    super(label);
    	    this.firstDate = firstDate;
    	    this.interval = interval;
    	}
    	
    	@Override
    	protected Date previousStandardDate(Date date, DateTickUnit unit) {
    		Date prevDate = removeDateTime(getOffsetDate(firstDate, -1 * interval));
    		Date standardDate = getOffsetDate(prevDate, interval);
    		
    		// Increments dates by the day-based interval until date <= standard date
    		while (date.after(standardDate)) {
    			prevDate = standardDate;
    			standardDate = getOffsetDate(standardDate, interval);
    		}
    		
    		return prevDate;
    	}
    	
    	@Override
    	protected Date nextStandardDate(Date date, DateTickUnit unit) {
			return getOffsetDate(previousStandardDate(date, unit), interval);
    	}
    }
}