/* MULTITHREADING <MyClass.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * Sammy Chien
 * sc55852
 * 16345
 * Slip days used: <0>
 * Fall 2018
 */
package assignment6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import assignment6.Theater.Seat;

public class BookingClient {
	
	private Map<String, Integer> offices;
	private Theater theater;
	private AtomicInteger clientID; // use AtomicInteger so you don't have to worry about concurrent modification of clientID
	
	
	public static void main(String[] args) {
		Map<String, Integer> office = new HashMap<String, Integer>(); // map: key = boxOfficeName; val = # of clients in line
		office.put("BX1", 10);
		office.put("BX3", 10);
		office.put("BX2", 10);
		office.put("BX5", 10);
		office.put("BX4", 10);
		Theater theater = new Theater(10, 5, "Ouija");
		BookingClient b = new BookingClient(office, theater);
		b.simulate();
	}
	
  /*
   * @param office maps box office id to number of customers in line
   * @param theater the theater where the show is playing
   */
  public BookingClient(Map<String, Integer> office, Theater theater) {
    // TODO: Implement this constructor
	  this.offices = office;
	  this.theater = theater;
	  this.clientID = new AtomicInteger(1);
  }

  /*
   * Starts the box office simulation by creating (and starting) threads
   * for each box office to sell tickets for the given theater
   *
   * @return list of threads used in the simulation,
   *         should have as many threads as there are box offices
   */
	public List<Thread> simulate() {
		//TODO: Implement this method
		List<Thread> threadList = new ArrayList<Thread>();
		/*
		 *  for every boxoffice in the office hashmap
		 *  	make a new thread that is based off of current boxoffice
		 *  		implement the run method in here
		 *  	add the thread to the threadlist
		 *  	start the thread
		 *  
		 */
		for (String boxOfficeName : this.offices.keySet()) {
			Thread boxOfficeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < offices.get(boxOfficeName); i++) { 
						// find best seat and assign it with a ticket
						// make sure this block of code is synchronized
						// use theatre as lock to ensure that no other box office can simultaneously access theater
						synchronized (theater) {
							Seat nextSeat = theater.bestAvailableSeat();
							if (nextSeat != null) {
								theater.printTicket(boxOfficeName, nextSeat, clientID.getAndIncrement());
							}
						}
					}
				}
			});
			threadList.add(boxOfficeThread);
		}
		for (Thread t : threadList) {
			t.start();
		}
		// find out when all the threads are dead (check .isAlive())
		AtomicBoolean allDead = new AtomicBoolean(false);
		while (allDead.get() == false) {
			allDead.set(true);
			for (Thread t : threadList) {
				if (t.isAlive()) {
					allDead.set(false); // if thread is alive, threads can't ALL be dead
				} else {
					allDead.set(allDead.get() & true); // if thread is dead, set alldead to true IFF all other threads are dead too
				}
			}
		}
		// now all threads are dead; no need to worry about concurrency
		if (theater.bestAvailableSeat() == null) 
			System.out.println("Sorry, we are sold out!");
		return threadList;
	}
}
