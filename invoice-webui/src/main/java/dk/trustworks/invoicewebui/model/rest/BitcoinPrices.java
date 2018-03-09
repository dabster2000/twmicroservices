package dk.trustworks.invoicewebui.model.rest;

public class BitcoinPrices
{
    private Dataset_data dataset_data;

    public Dataset_data getDataset_data ()
    {
        return dataset_data;
    }

    public void setDataset_data (Dataset_data dataset_data)
    {
        this.dataset_data = dataset_data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [dataset_data = "+dataset_data+"]";
    }
}
