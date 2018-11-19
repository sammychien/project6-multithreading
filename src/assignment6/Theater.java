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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Theater {
	private AtomicInteger seatsSold; // use AtomicInteger bc of concurrent modification worries
	private int totalSeats;
	private String show;
	private int numRows;
	private int seatsPerRow;
	private ArrayList<Seat> seatArray;
	private ArrayList<Ticket> ticketArray;
	private Object syncObj; // lock for printTicket and bestAvailableSeat

	/*
	 * Represents a seat in the theater A1, A2, A3, ... B1, B2, B3 ...
	 */
	static class Seat {
		private int rowNum;
		private int seatNum;

		public Seat(int rowNum, int seatNum) {
			this.rowNum = rowNum;
			this.seatNum = seatNum;
		}

		public int getSeatNum() {
			return seatNum;
		}

		public int getRowNum() {
			return rowNum;
		}

		@Override
		public String toString() {
			// TODO: Implement this method to return the full Seat location ex: A1
			String result = "";
			int tempRowNumber = rowNum + 1;
			do {
				tempRowNumber--;
				result = ((char) ('A' + tempRowNumber % 26)) + result;
				tempRowNumber = tempRowNumber / 26;
			} while (tempRowNumber > 0);
			result += seatNum;
			return result;
		}
	}

	/*
	 * Represents a ticket purchased by a client
	 */
	static class Ticket {
		private String show;
		private String boxOfficeId;
		private Seat seat;
		private int client;

		public Ticket(String show, String boxOfficeId, Seat seat, int client) {
			this.show = show;
			this.boxOfficeId = boxOfficeId;
			this.seat = seat;
			this.client = client;
		}

		public Seat getSeat() {
			return seat;
		}

		public String getShow() {
			return show;
		}

		public String getBoxOfficeId() {
			return boxOfficeId;
		}

		public int getClient() {
			return client;
		}

		public static final int ticketStringRowLength = 31;

		@Override
		public String toString() {
			String result, dashLine, showLine, boxLine, seatLine, clientLine, eol;

			eol = System.getProperty("line.separator");

			dashLine = new String(new char[ticketStringRowLength]).replace('\0', '-');

			showLine = "| Show: " + show;
			for (int i = showLine.length(); i < ticketStringRowLength - 1; ++i)
				showLine += " ";
			showLine += "|";

			boxLine = "| Box Office ID: " + boxOfficeId;
			for (int i = boxLine.length(); i < ticketStringRowLength - 1; ++i)
				boxLine += " ";
			boxLine += "|";

			seatLine = "| Seat: " + seat.toString();
			for (int i = seatLine.length(); i < ticketStringRowLength - 1; ++i)
				seatLine += " ";
			seatLine += "|";

			clientLine = "| Client: " + client;
			for (int i = clientLine.length(); i < ticketStringRowLength - 1; ++i)
				clientLine += " ";
			clientLine += "|";

			result = dashLine + eol + showLine + eol + boxLine + eol + seatLine + eol + clientLine + eol + dashLine;

			return result;

		}
	}

	public Theater(int numRows, int seatsPerRow, String show) {
		// TODO: Implement this constructor
		this.numRows = numRows;
		this.seatsPerRow = seatsPerRow;
		this.show = show;
		seatArray = new ArrayList<Seat>();
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 1; j < this.seatsPerRow + 1; j++) { // seat # starts with 1
				seatArray.add(new Seat(i, j));
			}
		}
		this.ticketArray = new ArrayList<Ticket>();
		this.seatsSold = new AtomicInteger(0);
		this.totalSeats = numRows * seatsPerRow;
		this.syncObj = new Object(); // create the lock
	}

	/*
	 * Calculates the best seat not yet reserved
	 *
	 * @return the best seat or null if theater is full
	 */
	public Seat bestAvailableSeat() {
		// TODO: Implement this method
		synchronized (syncObj) { // make sure that this block of code happens together
			if (this.seatsSold.get() >= this.totalSeats)
				return null;
			return this.seatArray.get(seatsSold.get()); //get next available seat based on current # of seats sold
		}
	}

	/*
	 * Prints a ticket for the client after they reserve a seat Also prints the
	 * ticket to the console
	 *
	 * @param seat a particular seat in the theater
	 * 
	 * @return a ticket or null if a box office failed to reserve the seat
	 */
	public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
		// TODO: Implement this method
		synchronized (syncObj) { // make sure everything here happens without yielding to anything else
			if (seat == null)
				return null;
			Ticket t = new Ticket(this.show, boxOfficeId, seat, client); // create new ticket
			System.out.println(t.toString()); // print ticket
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.ticketArray.add(t); // add ticket to ticketArray
			seatsSold.incrementAndGet(); // increment counter
			return t;
		}

	}

	/*
	 * Lists all tickets sold for this theater in order of purchase
	 *
	 * @return list of tickets sold
	 */
	public List<Ticket> getTransactionLog() {
		// TODO: Implement this method
		return this.ticketArray;
	}
}
