import rs.etf.sab.operations.*;
import rs.etf.sab.tests.*;
import student.*;


public class StudentMain {
    
    public static void main(String[] args) {
        CityOperations cityOperations = new bt200383_CityOperations();
        DistrictOperations districtOperations = new bt200383_DistrictOperations();
        CourierOperations courierOperations = new bt200383_CourierOperations(); 
        CourierRequestOperation courierRequestOperation = new bt200383_CourierRequestOperation();
        GeneralOperations generalOperations = new bt200383_GeneralOperations();
        UserOperations userOperations = new bt200383_UserOperations();
        VehicleOperations vehicleOperations = new bt200383_VehicleOperations();
        PackageOperations packageOperations = new bt200383_PackageOperations();
        
        
        TestHandler.createInstance(
            cityOperations,
            courierOperations,
            courierRequestOperation,
            districtOperations,
            generalOperations,
            userOperations,
            vehicleOperations,
            packageOperations);
        TestRunner.runTests();
        
    }
}
