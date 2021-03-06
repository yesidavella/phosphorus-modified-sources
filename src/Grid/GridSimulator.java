/*
 * This class extends the SimBaseSimulaotr, by giving it extra properties,
 * typical for grid simulations.
 */
package Grid;

import Grid.OCS.CircuitList;
import Grid.OCS.OCSRoute;
import Grid.Routing.Routing;
import Grid.Routing.RoutingViaJung;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import simbase.SimBaseSimulator;
import simbase.Stats.Logger;

/**
 *
 * @author Jens Buysse
 */
public class GridSimulator extends SimBaseSimulator {

    /**
     * The Routing component of the Simulator. Is used for routing algorithms
     * and has both physic and the optic topology.
     */
    private transient Routing routing;
    /**
     * Has ONLY the phisic topology, not optic.
     */
    private transient Routing physicTopology;
    /**
     * A list containing all the OCS routes which have been requested at the
     * moment. (OCs circuits in the network).
     */
    private CircuitList requestedCircuits = new CircuitList();
    /**
     * A list containing all the OCS routes which have been established in the
     * network.
     */
    private transient CircuitList establishedCircuits;

    /**
     * Defaultconstructor.
     */
    public GridSimulator() {
        super();
        this.resetAllStats();
        routing = new RoutingViaJung(this, "routing");
//        routing = new ShortesPathRouting(this);

        physicTopology = new RoutingViaJung(this, "physicTopology");
        logger = new Logger(12);
    }

    /**
     * Return the routing component of the simulator.
     *
     * @return The routing component of the simulator.
     */
    public Routing getRouting() {
        return routing;
    }

    /**
     * Sets the routing component of the simulator.
     *
     * @param routing The routing component of the simulator.
     */
    public void setRouting(Routing routing) {
        this.routing = routing;
    }

    /**
     * Prepares the routing object so that he can give the routing tables to the
     * enitities asking for it and prepares the physical Topology.
     */
    public void route() {
        establishedCircuits = new CircuitList();
        routing.route();
        physicTopology.route();
    }

    /**
     * Return the number of hops between the two other hops, according to the
     * routing sheme.
     *
     * @param source The source hop
     * @param destination The destination hop
     * @return The number of hops between source and destination.
     */
    public int getNrOfHopsBetween(Entity source, Entity destination) {
        return routing.getNrOfHopsBetween(source, destination);
    }

    /**
     * Add a OCS circuit setup request to the pending requests.
     *
     * @param route The route of the OCS circuit.
     * @return true if it worked, false if not.
     */
    public boolean addRequestedCircuit(OCSRoute route) {
        return requestedCircuits.add(route);
    }

    /**
     * Confirm that the requested circuit has been set up.
     *
     * @param route The route of the OCS circuit which has been set up.
     * @return True if confirmation was successfull, false if not.
     */
    //NOTA:Adicion de la ruta
    public boolean confirmRequestedCircuit(OCSRoute route) {
        if (requestedCircuits.contains(route)) {
            requestedCircuits.remove(route);
            routing.OCSCircuitInserted(route);

            if (establishedCircuits.add(route)) {
//                System.out.println("Inserto OCS entre " + route.getSource() + "->" + route.getDestination() + " con Color:" + route.getWavelength());
                return true;
            } else {
                System.out.println("Error INSERTANDO OCS entre " + route.getSource() + "->" + route.getDestination() + " con Color:" + route.getWavelength());
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Cancels a requested circuit in the network.
     *
     * @param route The route of the circuit that has to be cancelled.
     * @return True if cancellation worked.
     */
    public boolean cancelRequestedCircuit(OCSRoute route) {
        return requestedCircuits.remove(route);
    }

    /**
     * Returns wheter there exists a OCS circuit between the source and the
     * destination.
     *
     * @param source The source of the ocs-circuit.
     * @param destination The destiantion of the ocs-circuit.
     */
    public boolean ocsCircuitAvailable(Entity source, Entity destination) {
        Iterator<OCSRoute> it = establishedCircuits.iterator();
//        //System.out.println("GridSimulator  - source :" + source + " destination " + destination);
        while (it.hasNext()) {
            OCSRoute ocsRoute = it.next();
            if (ocsRoute.getSource().equals(source) && ocsRoute.getDestination().equals(destination)) {
                return true;
            }
        }
        return false;
    }
    HashSet<String> conx = new HashSet<String>();

    /**
     * Return the OCS route (circuit) between two entities.
     *
     * @param source The source of the circuit.
     * @param destination The destination of the circuit.
     * @return The ocs route between source and destination.
     */
    public List returnOcsCircuit(Entity source, Entity destination) {
        ArrayList<OCSRoute> circuits = new ArrayList();
        Iterator<OCSRoute> establishedCircuitsIt = establishedCircuits.iterator();

        while (establishedCircuitsIt.hasNext()) {
            OCSRoute ocsRoute = establishedCircuitsIt.next();
            if (ocsRoute.getSource().equals(source) && ocsRoute.getDestination().equals(destination)) {
                circuits.add(ocsRoute);
            }
        }
        if (circuits.isEmpty()) {
            return null;
        } else {
            return circuits;
        }
    }

    /**
     * Removes a circuit from the permanent circuits of the network.
     *
     * @param route The route of the OCS circuit which has been torn down.
     * @return true if removal worked, false if not.
     */
    public boolean circuitTearDown(OCSRoute route, int lambdaToSetFree) {

        int ocsIndexToRemove = -1;
        int circuitCounter = 0;

        for (OCSRoute establishedOCS : establishedCircuits) {

            if (establishedOCS.getWavelength() == lambdaToSetFree
                    && establishedOCS.getSource().equals(route.getSource())
                    && establishedOCS.getDestination().equals(route.getDestination())) {
//                OCSsToRemove.add(establishedOCS);
                ocsIndexToRemove = circuitCounter;
//                System.out.println("Saco OCS de la coleccion del simulador.");

                break;
            }
            circuitCounter++;
        }
        OCSRoute removedOCS = establishedCircuits.remove(ocsIndexToRemove);

        if (ocsIndexToRemove != -1) {
            return true;
        } else {
            throw new IllegalArgumentException("No se pudo remover OCS:" + route.toString());
        }
    }

    /**
     * Return just the physic topology, this topology edges only represents the
     * fibers. This topology is constants along all the simulation.
     *
     * @return physicTopology
     */
    public Routing getPhysicTopology() {
        return physicTopology;
    }

    public void setPhysicTopology(Routing physicTopology) {
        this.physicTopology = physicTopology;
    }

    public CircuitList getEstablishedCircuits() {
        return establishedCircuits;
    }
}
