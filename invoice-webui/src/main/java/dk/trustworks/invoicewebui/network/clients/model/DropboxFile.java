package dk.trustworks.invoicewebui.network.clients.model;

public class DropboxFile {

    private final byte[] fileAsByteArray;
    private final String filename;


    public DropboxFile(byte[] fileAsByteArray, String filename) {
        this.fileAsByteArray = fileAsByteArray;
        this.filename = filename;
    }

    public byte[] getFileAsByteArray() {
        return fileAsByteArray;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "DropboxFile{" +
                "filename='" + filename + '\'' +
                '}';
    }
}
