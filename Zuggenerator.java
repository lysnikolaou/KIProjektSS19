import java.util.*;



public class Zuggenerator {
	
	private int [][] board2=new int [7][8];
	
	public  LinkedList<String> zuege ( String board, boolean spieler ) {
		
		LinkedList<String> zuege=new LinkedList<String>();
		setBoard (board);
		
		for (int i=0;i<board2.length;i++) {
			for (int b=0; b<board2[1].length;b++) {
				if (board2[i][b]==2 && spieler == false) {
					for (int x=-1; x<=1;x++) {
						for (int y=-1;y<=1;y++) {
							if (x!=0 || y!=0) {
								if (i+x >=0 && i+x < 7 && b+y >=0 && b+y < 8 ) {
									if (board2[i+x][b+y]==3 || board2[i+x][b+y]==4) {
										zuege.add(kords(b,i,b+y,i+x));
									}
									
									
								}
								
								if (i+x+x >=0 && i+x+x < 7 && b+y+y >=0 && b+y+y < 8 ) {
									if ( ( board2[i+x][b+y]==0 || board2[i+x][b+y]==1 ) && ( board2[i+x+x][b+y+y]==0 || board2[i+x+x][b+y+y]==1 ) )
										zuege.add(kords(b,i,b+y+y,i+x+x));
								}
								
							}
						}
					}
				}
				
				if (board2[i][b]==4 && spieler == true) {
					for (int x=-1; x<=1;x++) {
						for (int y=-1;y<=1;y++) {
							if (x!=0 || y!=0) {
								if (i+x >=0 && i+x < 7 && b+y >=0 && b+y < 8 ) {
									if (board2[i+x][b+y]==1 || board2[i+x][b+y]==2) {
										zuege.add(kords(b,i,b+y,i+x));
									}
									
									
								}
								if (i+x+x >=0 && i+x+x < 7 && b+y+y >=0 && b+y+y < 8 ) {
									if ( ( board2[i+x][b+y]==0 || board2[i+x][b+y]==3 ) && ( board2[i+x+x][b+y+y]==0 || board2[i+x+x][b+y+y]==3 ) )
										zuege.add(kords(b,i,b+y+y,i+x+x));
								}
								
							}
						}
					}
				}
				
				
			}
		}
		
		
		
		
		return zuege;
	}


public String kords ( int x1,int y1, int x2, int y2 ) {
	String move="";
	x1=x1+97;
	x2=x2+97;
	y1=7-y1;
	y2=7-y2;
	move = move+(char)x1;
	move = move+y1;
	move = move +"-";
	move = move+(char)x2;
	move = move+y2;
	return move;
	
	
}

private  void setBoard(String state) {

	//TODO: implement
	int a=0;
	int b=0;
	for ( int i=0; i<state.length();i++) {
		if ( state.charAt(i) == 'g' ||  state.charAt(i) == 'G' || state.charAt(i) == 'r'  || state.charAt(i) == 'R' ) {
			int x = number (state.charAt(i));
			board2 [a][b]=x;
			b++;
		}
		
		
		else if ( state.charAt(i) == '/' ) {
			a++;
			b=0;
		}
		
		else {
			b = b + Character.getNumericValue(state.charAt(i));
		}
		
	}

}


private  int number ( char c ) {
	if ( c == 'g' ) {
		return 1;
	}
	else if ( c == 'G' ) {
		return 2;
	}
	else if ( c == 'r' ) {
		return 3;
	}
	else  {
		return 4;
	}

}

// kann das Array wieder in einen Fen-String konvertieren

public String converter (int [][] array) {
		String s="";
		int a=0;
		for (int b=0; b<array.length;b++) {
			for (int c=0; c<array[1].length;c++) {
				if (c==0 ) {
					a=0;
					if ( b!=0) {
						s=s+"/";
				}
				}
				
				if (array[b][c]==1) {
					if (a!=0) {
						s=s+a;
					}
					s=s+"g";
					a=0;
					
				}
				if (array[b][c]==2) {
					if (a!=0) {
						s=s+a;
					}
					s=s+"G";
					a=0;
					
				}
				if (array[b][c]==3) {
					if (a!=0) {
						s=s+a;
					}
					s=s+"r";
					a=0;
					
				}
				if (array[b][c]==4) {
					if (a!=0) {
						
						s=s+a;
						
					}
					s=s+"R";
					a=0;
					
				}
				
				if (array[b][c]==0) {

					a=a+1;
	

					
				}
				
				if (array[b][c]==0 && a!=8 && c==7) {
					s=s+a;
				}
			
		}
	}
		return s;
	
}

}