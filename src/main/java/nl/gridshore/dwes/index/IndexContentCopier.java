package nl.gridshore.dwes.index;

/**
 * Interface for implementations that copy all the data from the fromIndex to the toIndex.
 */
public interface IndexContentCopier {
    /**
     * Start the actual copying.
     * @param fromIndex String containing the name of the index to copy from
     * @param toIndex String containing the name of the index to copy to
     */
    void execute(String fromIndex, String toIndex);
}
