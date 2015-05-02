package nl.gridshore.dwes.index.api;

import java.util.List;

/**
 * Service class to interact with the indexes of the cluster.
 */
public interface IndexManager {
    /**
     * Returns a list of all indexes and some basic information about the indexes.
     *
     * @return List with all the available indexes
     */
    List<ElasticIndex> obtainIndexes();

    /**
     * Change settings of the index as provided by the ChangeIndexRequest.
     *
     * @param request Contains the properties to change
     */
    void changeIndexSettings(ChangeIndexRequest request);

    /**
     * Remove the provided index
     *
     * @param index String containing the name or pattern of the index/indexes to delete
     */
    void removeIndex(String index);

    /**
     * // TODO check if we can have multiple indexes at the same time using  a wildcard
     * Closes the index with the provided name
     *
     * @param index String containing the name of the index to close
     */
    void closeIndex(String index);

    /**
     * Open the index with the provided name.
     *
     * @param index String containing the name of the index to open
     */
    void openIndex(String index);

    /**
     * Request the provided index to be optimized. Parameters in the reuqest can be used to tailor the optimization.
     *
     * @param request Object that contains the index to optimize as well as the additional parameters
     */
    void optimizeIndex(OptimizeIndexRequest request);

    /**
     * Method used to copy an index into a new index. Some configurations are available using the CopyIndexRequest
     * object.
     *
     * @param request Object that contains the parameters for the copy request.
     */
    void copyIndex(CopyIndexRequest request);

    /**
     * Create an alias for the provided index, this means we copy the index with a timestamp name and add an alias the
     * name of the old index.
     *
     * @param index String containing te name of the index to create an alias for.
     */
    void createAliasFor(String index);
}
