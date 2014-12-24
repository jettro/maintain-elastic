package nl.gridshore.dwes.elastic;

/**
 * Created by jettrocoenradie on 24/12/14.
 */
public interface IndexContentCopier {
    void execute(String fromIndex, String toIndex);
}
