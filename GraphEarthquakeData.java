package cmsc256;

import bridges.base.*;
import bridges.connect.Bridges;
import bridges.data_src_dependent.EarthquakeUSGS;
import bridges.connect.DataSource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GraphEarthquakeData {

  public static double calcDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		final int radius = 6371; // Radius of the earth in km

		// Haversine formula to calculate a value between 0 and 1 between 2 points on a sphere,
		//  1 being the opposite side of the sphere
		double laDistance = Math.toRadians(latitude2 - latitude1);
		double loDistance = Math.toRadians(longitude2 - longitude1);

		double a = Math.sin(laDistance / 2) * Math.sin(laDistance / 2)
				+ Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
				* Math.sin(loDistance / 2) * Math.sin(loDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double distance = radius * c;    //convert to km
		return distance;
	}


  public static <E> void main(String[] args) throws Exception {
	// Create a Bridges object
	  Bridges bridges = new Bridges (14, "fergusonba", "757857963875");
	// Get a DataSource object from Bridges
	  DataSource ds = bridges.getDataSource();
    	// Set an assignment title
	bridges.setTitle("Earthquake Data Graph Lab");
	bridges.setDescription("CMSC 256, Fall 2022");

	// Initialize a Graph
	GraphAdjListSimple<String> graph = new GraphAdjListSimple<>();


   /*
    * Grab Earthquake data and store it in a List
    * Sort the list by magnitude
    * Retain only 10 earthquakes of highest magnitude
    */

	  List<EarthquakeUSGS> eqList = ds.getEarthquakeUSGSData(5000);
	  Collections.sort(eqList, new Comparator<EarthquakeUSGS>() {
		  public int compare(EarthquakeUSGS eq1, EarthquakeUSGS eq2) {
			  return Double.compare(eq2.getMagnitude(), eq1.getMagnitude());
		  }
	  });

	  eqList = eqList.subList(0, 20);






    /*
    * Add the Earthquakes to the graph
    * Set each earthquake's location based on its latitude and longitude
    * ex: graph.getVisualizer(key).setLocation(earthquake.getLongit(), earthquake.getLatit());

    */

	  for (EarthquakeUSGS eq : eqList) {

		  // Add the vertex to the graph
		  String eqID = eq.getTitle();
		  graph.addVertex(eqID, eq.getProperties());
		  graph.getVisualizer(eqID).setLocation(eq.getLongit(), eq.getLatit());

		  graph.getVertex(eq.getTitle()).getVisualizer().setSize(10f);
		  graph.getVertex(eq.getTitle()).getVisualizer().setColor("red");
		  graph.getVertex(eq.getTitle()).getVisualizer().setOpacity(0.8f);
	  }

    bridges.setCoordSystemType("equirectangular");
    bridges.setDataStructure(graph);
    bridges.setMapOverlay(true);
    bridges.setMap("world", "all");
    bridges.setTitle("Earthquake Map");
    bridges.visualize();


    /*
    * Compare the distances between all vertexes in the graph, drawing an edge
    * if they are within 1000km. A method is provided to give a rough
    * estimate between 2 lat,long points.
    *
    * example usage: calcDistance(eq1.getLatit(), eq1.getLongit(),
    *                eq2.getLatit(), eq2.getLongit());
    * which returns a double representing the distance of two points in km
    */

	  for (int i = 0; i < eqList.size(); i++) {
		  EarthquakeUSGS eq1 = eqList.get(i);
		  for (int j = i + 1; j < eqList.size(); j++) {
			  EarthquakeUSGS eq2 = eqList.get(j);

			  double dist = calcDistance(eq1.getLatit(), eq1.getLongit(), eq2.getLatit(), eq2.getLongit());

			  if ((int) dist > 10000) {
				  graph.addEdge(eq1.getTitle(), eq2.getTitle(), "");
			  }
		  }
	  }


	  bridges.visualize();

    /*
    * Reset the locations of the vertices by setting their location to
    * Double.POSITIVE_INFINITY
    *
    * ex: graph.getVisualizer(key).setLocation(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    */
	  for (EarthquakeUSGS eq : eqList) {
		  graph.getVisualizer(eq.getTitle()).setLocation(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	  }


    bridges.setMapOverlay(false);
    bridges.visualize();


  }
}
