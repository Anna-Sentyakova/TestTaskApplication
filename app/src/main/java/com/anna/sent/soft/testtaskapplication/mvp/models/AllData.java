package com.anna.sent.soft.testtaskapplication.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AllData implements Parcelable {
    public final static Parcelable.Creator<AllData> CREATOR = new Creator<AllData>() {
        public AllData createFromParcel(Parcel in) {
            return new AllData(in);
        }

        public AllData[] newArray(int size) {
            return new AllData[size];
        }
    };
    @SerializedName("response")
    @Expose
    private List<Employee> employees = new ArrayList<>();
    private List<Speciality> availableSpecialities;
    private Map<Speciality, List<Employee>> employeesMap;

    public AllData() {
    }

    public AllData(Parcel in) {
        in.readList(employees, Employee.class.getClassLoader());
    }

    public AllData(List<Employee> employees) {
        super();
        if (employees != null)
            this.employees = new ArrayList<>(employees);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Speciality> getSpecialities() {
        if (employees == null)
            return Collections.emptyList();

        if (availableSpecialities != null)
            return availableSpecialities;

        initCollections();
        return availableSpecialities;
    }

    public Map<Speciality, List<Employee>> getEmployeesMap() {
        if (employees == null)
            return Collections.emptyMap();

        if (employeesMap != null)
            return employeesMap;

        initCollections();
        return employeesMap;
    }

    private void initCollections() {
        availableSpecialities = Stream.of(employees)
                .flatMap(employee -> Stream.of(employee.getSpecialities()))
                .distinct()
                .sortBy(Speciality::getName)
                .collect(Collectors.toList());
        employeesMap = Stream.of(availableSpecialities).collect(Collectors.toMap(
                speciality -> speciality,
                speciality -> new ArrayList<>(
                        Stream.of(employees)
                                .filter(e -> e.getSpecialities().contains(speciality))
                                .map(EmployeeStringUtils::new)
                                .sortBy(EmployeeStringUtils::nameToCompare)
                                .map(EmployeeStringUtils::getEmployee)
                                .collect(Collectors.toList()))));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllData allData = (AllData) o;

        return this.employees != null ? this.employees.equals(allData.employees) : allData.employees == null;
    }

    @Override
    public int hashCode() {
        return employees != null ? employees.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(employees);
    }

    public int describeContents() {
        return 0;
    }
}
