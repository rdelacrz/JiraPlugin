package plugin_suite.charts;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.charts.util.ChartUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.I18nHelper;

public class TimeTrendingChart {
	private String title;
	private String seriesName;
	private String timeAxisLabel;
	private String valueAxisLabel;
	
	/**
	 * Generic constructor that sets default title and label information.
	 */
	public TimeTrendingChart() {
		title = "Date Field Trending Chart";
		seriesName = "Issues";
		timeAxisLabel = "Dates";
    	valueAxisLabel = "Number of Issues";
	}
	
	public TimeTrendingChart(String title, String timeAxisLabel, String valueAxisLabel) {
		this.title = title;
		this.timeAxisLabel = timeAxisLabel;
		this.valueAxisLabel = valueAxisLabel;
		seriesName = "Issues";		// Sets series name to default "Issues"
	}
	
	public Chart generateChart(int width, int height, int dateInterval, int dataRange, List<Long> dates, List<Long> counts) {
		// Initializes data set for the chart
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries series = new TimeSeries(seriesName);
		
		// Should be equal in length, but will take the smaller length of the two, if they aren't equal
		int length = dates.size() <= counts.size() ? dates.size() : counts.size();
		
		// Gets upper bound of chart - today's date
		Date upperBound = TimeTrendingChartGenerator.removeDateTime(new Date());
		
		// Initializes map of days to counts
		Map<Day, Long> map = new HashMap<Day, Long>();
		
		// Obtains the data points for the chart
		Calendar calendar = new GregorianCalendar();
		for (int i = 0; i < length; i++) {
			// Gets date based on list of milliseconds
			Date date = new Timestamp(dates.get(i));
			
			// Gets the axis date and sets it within the calendar
			calendar.setTime(getAxisDate(date, upperBound, dateInterval));
			
			// Gets parameters
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1;
			int year = calendar.get(Calendar.YEAR);
			
			// Gets Day object then either maps count or updates existing count
			Day axisDay = new Day(day, month, year);
			if (!map.containsKey(axisDay))
				map.put(axisDay, counts.get(i));
			else
				map.put(axisDay, map.get(axisDay) + counts.get(i));
		}
		
		// Iterates through map and adds each count to the series
		for (Day axisDay : map.keySet())
			series.add(axisDay, map.get(axisDay));
		
		// Adds final series to the data set
		dataset.addSeries(series);
		
    	// Creates initial generator responsible for generating a Time Trending Chart
		TimeTrendingChartGenerator generator = new TimeTrendingChartGenerator(
				title, dataset, timeAxisLabel, valueAxisLabel, width, height, dateInterval, 
				dataRange, upperBound, getI18nHelper());
		
		// Gets the chart helper and uses it to generate the time trending chart
		ChartHelper helper = generator.generateChart();
		
		// Initializes the parameters mappings for the chart
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("chart", helper.getLocation());
		params.put("chartDataset", dataset);
		params.put("imagemap", helper.getImageMap());
	    params.put("imagemapName", helper.getImageMapName());
	    params.put("imageWidth", Integer.valueOf(width));
	    params.put("imageHeight", Integer.valueOf(height));
	    
	    // Sets base 64 image, if method exists
	    try {
	    	String base64Image = ((ChartUtils) ComponentAccessor.getComponent(ChartUtils.class))
		    		.renderBase64Chart(helper.getImage(), title);
	        params.put("base64Image", base64Image);
	    } catch (NoSuchMethodError e) {
	    	e.printStackTrace();
	    }
	    
	    return new Chart(helper.getLocation(), helper.getImageMap(), helper.getImageMapName(), params); 
	}
	
	/**
	 * Determines the axis date corresponding to the given date, based on the upper bound and 
	 * date interval.
	 * 
	 * @param date - Date to be associated with the axis date.
	 * @param upperBound - Highest date on the axis.
	 * @param dateInterval - Number of days between each date on the axis.
	 * @return Axis date associated with the given date and is always equal to the upper bound date
	 * minus the date interval multiplied by some integer.
	 */
	private Date getAxisDate(Date date, Date upperBound, int dateInterval) {
		Date timelessDate = TimeTrendingChartGenerator.removeDateTime(date);
		long diffMilli = upperBound.getTime() - timelessDate.getTime();
		long diffDays = (long) Math.ceil(diffMilli / (1000*60*60*24));
		
		// Calculates offset (in milliseconds)
		long offset = (diffDays % dateInterval) * (1000*60*60*24);
		
		// Offsets date by given number of days
		return new Date(timelessDate.getTime() + offset);
	}
	
	private I18nHelper getI18nHelper() {
		return ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
	}
}
