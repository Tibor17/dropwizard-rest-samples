package rest;

import data.XyzReport;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

public class MyService {
    private final XyzReport xyzReport = new XyzReport();

    public MyService() {
        xyzReport.setSomeData(3L);
    }

    public XyzReport filterXyzReport(long id) {
        return xyzReport.getSomeData() == id ? xyzReport : null;
    }

    public XyzReport produceError() {
        throw new OptimisticLockException();
    }

    public XyzReport noContent() {
        throw new NoResultException();
    }
}
