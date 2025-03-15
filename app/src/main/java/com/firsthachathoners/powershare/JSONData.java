package com.firsthachathoners.powershare;

/**
 * Created by safa on 17.02.2018.
 * edited by KofiTkay on 14th of March 2025
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a container for API response data containing multiple Result objects
 */
public class JSONData {

    @SerializedName("result")
    @Expose
    private List<Result> result = Collections.emptyList();  // Initialize with empty list

    public JSONData() {}  // Default constructor

    public JSONData(List<Result> result) {
        this.result = result != null ? result : Collections.emptyList();
    }

    /**
     * @return Unmodifiable list of results to prevent external modification
     */
    public List<Result> getResult() {
        return Collections.unmodifiableList(result);
    }

    public void setResult(List<Result> result) {
        this.result = result != null ? result : Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONData jsonData = (JSONData) o;
        return Objects.equals(result, jsonData.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        return "JSONData{" +
                "result=" + result +
                '}';
    }
}