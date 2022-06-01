package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.dto.CompanyAggregateData;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.network.rest.BiRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BiService {

    private final BiRestService biRestService;

    @Autowired
    public BiService(BiRestService biRestService) {
        this.biRestService = biRestService;
    }

    @Cacheable("CompanyAggregateData")
    public List<CompanyAggregateData> getCompanyAggregateDataByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return biRestService.getCompanyAggregateData(periodStart, periodEnd).stream().sorted(Comparator.comparing(CompanyAggregateData::getMonth)).collect(Collectors.toList());
    }

    @Cacheable("EmployeeAggregateData")
    public List<EmployeeAggregateData> getEmployeeAggregateDataByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return biRestService.getEmployeeAggregateData(periodStart, periodEnd).stream().sorted(Comparator.comparing(EmployeeAggregateData::getMonth)).collect(Collectors.toList());
    }
}
