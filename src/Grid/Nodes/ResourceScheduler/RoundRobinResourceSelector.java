/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Grid.Nodes.ResourceScheduler;

import Grid.Entity;
import Grid.Interfaces.Messages.JobAckMessage;
import Grid.Interfaces.ResourceNode;
import Grid.Interfaces.ResourceSelector;
import Grid.Nodes.PCE;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Jens Buysse
 */
public class RoundRobinResourceSelector implements ResourceSelector, Serializable {

    /**
     * The list of resources this selector is responsible for.
     */
    private List<ResourceNode> resources;
    /**
     * The last returned resource.
     */
    private int index;

    /**
     * Constructor
     * @param resources The list with resources this resource is responsible for.
     */
    public RoundRobinResourceSelector(List<ResourceNode> resources) {
        this.resources = resources;
    }

    /**
     * Will find the best resource to schedule the next job to.
     * @param resources The list of resources where one has to be chosen from.
     * @return The scheduled resource if available, null i none were found.
     */
    public ResourceNode findBestResource(Entity sourceNode, List<ResourceNode> resources,double jobFlop,PCE pce,JobAckMessage job) {
        //int nrOfResources = resources.size();
        //int processed = 0;
        //while (processed <= nrOfResources) {
            if (!resources.isEmpty()) {
                if (index == resources.size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
          //      if (resources.get(index).getQueuingSpace() > 0) {
                    return resources.get(index);
            //    } else {
              //      processed++;
                //}
            }
        //}
        return null;
    }

    public ResourceNode findBestresource(double jobFlops) {
        return findBestResource(null, resources, jobFlops,null,null);
    }
}
