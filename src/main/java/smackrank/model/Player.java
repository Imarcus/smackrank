package smackrank.model;

import smackrank.EloRater;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player implements Rated{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int rating;
	private String name;
	private String colour;

	protected Player(){}

	public Player(String name, String colour) {
		this.rating = EloRater.INITIAL_RATING;
		this.name = name;
		this.colour = colour;
	}

	public int getRating() {
		return rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	@Override
	public String toString() {
		return String.format(
				"Player[name='%s', rating='%d']", name, rating);
	}
}