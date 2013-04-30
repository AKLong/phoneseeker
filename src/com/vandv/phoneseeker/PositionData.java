package com.vandv.phoneseeker;

import java.util.Date;
import static com.vandv.phoneseeker.CommonUtilities.dateFormat;

public class PositionData {

	//private variables
	Long _id;
	Long _serverid;
	Double _gpslat;
	Double _gpslon;
	Double _netlat;
	Double _netlon;
	String _ip;
	String _status;
	Float _gpsaccuracy;
	Float _netaccuracy;
	String _source;
	Date _date;

	// Empty constructor
	public PositionData(){
		this._gpslat=0.0;
		this._gpslon=0.0;
		this._gpsaccuracy=(float) 0.0;
		this._netlat=0.0;
		this._netlon=0.0;
		this._netaccuracy=(float) 0.0;
		this._ip="undefined";
		this._source="undefined";
		this.setDate(); 
	}
	/*    // constructor
    public PositionData(Long id, Double lat, Double lon){
        this._id = id;
        this._lat = lat;
        this._lon = lon;
        this.setDate();
    }

    // constructor
    public PositionData( Double lat, Double lon){
        this._lat = lat;
        this._lon = lon;
        this.setDate();
     }

	 */
	// getting ID
	public Long getID(){
		return this._id;
	}

	// setting id
	public void setID(Long id){
		this._id = id;
	}

	// getting ID
	public Long getServerID(){
		return this._serverid;
	}

	// setting id
	public void setServerID(Long serverId){
		this._serverid = serverId;
	}

	public String getDate(){

		return  dateFormat.format(this._date);
	}

	// setting id
	public void setDate(){
		this._date = new Date();
	}

	public void setDate(Date d){
		this._date = d;
	}

	// getting name
	public Double getNetworkLat(){
		return this._netlat;
	}

	// setting name
	public void setNetworkLat(Double lat){
		this._netlat = lat;
	}

	public Double getNetworkLon(){
		return this._netlon;
	}

	public void setNetworkLon(Double lon){
		this._netlon = lon;
	}

	// getting name
	public Double getGPSLat(){
		return this._gpslat;
	}

	// setting name
	public void setGPSLat(Double lat){
		this._gpslat = lat;
	}

	public Double getGPSLon(){
		return this._gpslon;
	}

	public void setGPSLon(Double lon){
		this._gpslon = lon;
	}

	public String getIP(){
		return this._ip;
	}

	// setting id
	public void setIP(String ip){
		this._ip = ip;
	}

	public String getStatus(){
		return this._status;
	}

	// setting id
	public void setStatus(String status){
		this._status =status;
	}

	public String getSource(){
		return this._source;
	}

	// setting id
	public void setSource(String source){
		this._source = source;
	}

	public Float getGPSAccuracy(){
		return this._gpsaccuracy;
	}

	// setting id
	public void setGPSAccuracy(Float accuracy){
		this._gpsaccuracy = accuracy;
	}

	public Float getNetworkAccuracy(){
		return this._netaccuracy;
	}

	// setting id
	public void setNetworkAccuracy(Float accuracy){
		this._netaccuracy = accuracy;
	}



}
