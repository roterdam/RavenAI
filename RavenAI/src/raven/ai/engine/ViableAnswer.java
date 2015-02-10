package raven.ai.engine;

public class ViableAnswer implements Comparable {
	public Figure AnswerFigure;
	public int Score;
	public boolean Incompatible = false;
	
	public ViableAnswer(Figure answer, int score) {
		this.AnswerFigure = answer;
		this.Score = score;
	}

	@Override
	public int compareTo(Object otherAnswer) {
		return ((ViableAnswer)otherAnswer).Score - this.Score;		
	}
		
}
