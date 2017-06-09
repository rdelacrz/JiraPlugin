package plugin_suite.resources;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.atlassian.jira.charts.Chart;

import plugin_suite.charts.TimeTrendingChart;
import plugin_suite.models.ChartRestResourceModel;

@Path("/charts")
public class ChartRestResource {
	
	@GET
	@Path("/generate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response generate(
			@QueryParam("width") @DefaultValue("600") int width,
            @QueryParam("height") @DefaultValue("300") int height,
            @QueryParam("dateInterval") int dateInterval,
            @QueryParam("dataRange") int dataRange,
            @QueryParam("date") List<Long> dates, @QueryParam("count") List<Long> counts,
            @QueryParam("title") @DefaultValue("Date Field Trending Chart") String title,
            @QueryParam("timeAxisLabel") @DefaultValue("Dates") String timeAxisLabel,
            @QueryParam("valueAxisLabel") @DefaultValue("Number of Issues") String valueAxisLabel) {
		// Produces chart based on passed parameters
		Chart chart = getChart(width, height, dateInterval, dataRange, dates, counts, 
				title, timeAxisLabel, valueAxisLabel);
		return Response.ok(
			new ChartRestResourceModel(chart.getLocation(), chart.getImageMap(), chart.getImageMapName(), 
					width, height, (String) chart.getParameters().get("base64Image"))
		).build();
	}
	
	/**
	 * Gets the resulting Chart object, based on the parameters passed.
	 * 
	 * @param width - Width of the chart.
	 * @param height - Height of the chart.
	 * @param dateInterval - Number of days between each date within the time-based axis of the chart.
	 * @param dataRange - Range of data (in days) represented by the time-based axis of the chart.
	 * @param dates - List of numbers that represent the number of milliseconds since 1/1/1970. Is 
	 * meant for generating Date objects by passing the number into the constructor.
	 * @param counts - List of counts associated with the dates. This list is expected to be equal in 
	 * length to the dates parameter.
	 * @param title - Title of chart.
	 * @param timeAxisLabel - Label for time axis (the x-axis).
	 * @param valueAxisLabel - Label for value axis (the y-axis).
	 * @return Chart object generated using the given parameters.
	 */
	public Chart getChart(int width, int height, int dateInterval, int dataRange, List<Long> dates, 
			List<Long> counts, String title, String timeAxisLabel, String valueAxisLabel) {
		TimeTrendingChart timeTrendingChart = new TimeTrendingChart(title, timeAxisLabel, valueAxisLabel);
		return timeTrendingChart.generateChart(width, height, dateInterval, dataRange, dates, counts);
	}
}
