package com.hm

import java.io.File
import java.net.InetAddress

import com.hm.connector.MysqlClient
import com.maxmind.geoip2.DatabaseReader

/**
  * Created by hari on 3/3/17.
  */
object Test {
  val database = new File("/home/hari/Downloads/GeoLite2-City.mmdb");
  def main(args: Array[String]): Unit = {

  }
  def func={


    // This creates the DatabaseReader object, which should be reused across
    // lookups.
    val reader = new DatabaseReader.Builder(database).build();

    val ipAddress = InetAddress.getByName("8.8.4.4" +
      "" +
      "");

    // Replace "city" with the appropriate method for your database, e.g.,
    // "country".
    val response = reader.city(ipAddress);

    val country = response.getCountry();
    println(country.getIsoCode());            // 'US'
    println(country.getName());               // 'United States'
    println(country.getNames().get("zh-CN")); // '美国'

    val subdivision = response.getMostSpecificSubdivision();
    println(subdivision.getName());    // 'Minnesota'
    println(subdivision.getIsoCode()); // 'MN'

    val city = response.getCity();
    println(city.getName()); // 'Minneapolis'

    val postal = response.getPostal();
    println(postal.getCode()); // '55455'

    val location = response.getLocation();
    println(location.getLatitude());  // 44.9733
    println(location.getLongitude()); // -93.2323

  }
}
