/*
 * The Interface Sender is responsible for putting messages on the links.
 */
package Grid.Sender;

import Grid.Entity;
import Grid.GridSimulation;
import Grid.GridSimulator;
import Grid.Interfaces.Messages.GridMessage;
import Grid.Port.GridOutPort;
import Grid.Utilities.Config;
import java.io.Serializable;
import simbase.Time;

/**
 *
 * @author Jens Buysse
 */
public abstract class Sender implements Serializable {

    /**
     * The owner of this sender.
     */
    protected Entity owner;
    /**
     * The Gridsimulator
     */
    protected GridSimulator simulator;
    /**
     * La variable minNumberChannels nunca puede ser menor a 1;
     */
    private static final int minNumberChannels = 3;
    /**
     * Min. bandwidth to grant inside a OCS. This is the width of a channel.
     */
    public static final double MIN_BANDWIDHT_TO_GRANT = 0.03 * GridSimulation.configuration.getDoubleProperty(Config.ConfigEnum.linkSpeed);//units Mbps
    public static final int INVALID_BANDWIDHT = -1;
    /**
     * Indicates if the AG2 Resource selector is selected or not.
     */
    private transient static boolean ag2ResourceSelectorSelected = false;

    /**
     * Constructor
     *
     * @param owner
     */
    public Sender(Entity owner, GridSimulator simulator) {
        this.owner = owner;
        this.simulator = simulator;
    }

    /**
     * Send a message into the network. This method must be implemented by every
     * class who extends Sender.
     *
     * @param message The message to send
     * @param t The portFreeAgainTime when to send the message
     * @param source The source which sends the message
     * @param destination The destination of the message
     * @param outputFail Output the failures? This is set to false when hybrid
     * switching is used
     * @return True if sending worked, False if not.
     */
    public abstract boolean send(GridMessage message, Time t, boolean outputFail);

    public boolean putMsgOnLink(GridMessage message, GridOutPort port, Time t, boolean isTheHeadOCS, int hopsOCS) {
        throw new AbstractMethodError("Se llamo en la clase Sender el metodo putMsgOnLink sin implementar.");
    }

    ;

    /**
     * Returns the simulator this sender belongs to.
     * @return The simulator of this sender.
     */
    public GridSimulator getSimulator() {
        return simulator;
    }

    /**
     * Sets the simulator this sender belongs to.
     *
     * @param simulator The new simulator to which this sender belongs to.
     */
    public void setSimulator(GridSimulator simulator) {
        this.simulator = simulator;
    }

    /**
     * Returns the owner of this class.
     *
     * @return the owner of this class.
     */
    public Entity getOwner() {
        return owner;
    }

    /**
     * Set the owner of this sender.
     *
     * @param owner The new owner of this sender.
     */
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    /**
     * Sugiere el ancho de banda a asignar teniendo en cuenta los sigs
     * parametros. El minimo ancho de banda q asigna es de 1Mbps y el maximo
     * availableBandwith si availableBandwith es mayor q 1. Si no puede hacer el
     * calculo, ó, almenos no se puede asignar un ancho de banda >= 1 retorna
     * -1.
     *
     * @param availableBandwith in Mbps.
     * @param trafficPriority
     * @param numberOfChannels
     * @return Ancho de banda sugerido en Mbps siempre y cuando este sea mayor o
     * igual q 1Mbps, si no -1.
     */
    public static double getBandwidthToGrant(double availableBandwith, int trafficPriority, int numberOfChannels) {

        double bandwithToGrant = -1;
        double pendant = 0;
        double constant = 0;

        if (trafficPriority < 1 || trafficPriority > 10) {
            throw new IllegalArgumentException("La prioridad debe ser un numero NATURAL entre 1 y 10");
        }

        /**
         * Enfoque general
         */
        pendant = (availableBandwith * (numberOfChannels + minNumberChannels - 1)) / (9 * (numberOfChannels + minNumberChannels));
        constant = (availableBandwith * (10 - numberOfChannels - minNumberChannels)) / (9 * (numberOfChannels + minNumberChannels));

        /*
         * Dejo la ecuacion de la forma y=mx+c; donde m=pendiente=pendant
         */
        bandwithToGrant = (pendant * trafficPriority) + constant;
        //Nunca se asignan anchos de banda inferiores a 1
        return ((bandwithToGrant < MIN_BANDWIDHT_TO_GRANT) ? INVALID_BANDWIDHT : bandwithToGrant);
    }

    public static boolean isAg2ResourceSelectorSelected() {
        return ag2ResourceSelectorSelected;
    }

    public static void setAg2ResourceSelectorSelected(boolean ag2ResourceSelectorSelected) {
        Sender.ag2ResourceSelectorSelected = ag2ResourceSelectorSelected;
    }
}
