package hw2;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.*;

class ConvexHull extends Frame implements ActionListener, TextListener{
	static ConvexHull frm = new ConvexHull();
	static Button btn = new Button("Send");
	static TextArea txa = new TextArea("請輸入要幾個點：");
	static TextArea txa1 = new TextArea("Convex Hull");
	static Label lab1 = new Label("Convex Hull");
	int amount;  //接收由文字方塊輸入的數字

	//定義一個 Vertex 類別，放置 x y 形成一個座標
	private static class Vertex {
		public int x;
		public int y;

		//建構子，可以直接拿來放 x y
		public Vertex(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
  	/*----------------------------------------------*/



	/*-------------程式的 Entry Point---------------*/
	public static void main(String args[]){
		btn.addActionListener(frm);  
		txa.addTextListener(frm);  


		frm.setLayout(null);  
		frm.setTitle("Convex Hull"); 
		btn.setBounds(225,500,200,50); 
		txa.setBounds(10,500,225,50);  
		lab1.setBounds(100,30,300,40); 
		lab1.setBackground(Color.yellow);  
		txa1.setEditable(false);  
		frm.setSize(500,550);  
		frm.add(btn);  
		frm.add(txa); 
		frm.add(lab1); 
		frm.setVisible(true);  
		frm.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				frm.dispose();
			}
		});
	}
	/*-----------------------------------------------*/



	/*--------------------處理事件-------------------*/
	//處理 button 的事件
	public void actionPerformed(ActionEvent e){
		Graphics g = getGraphics();
		update(g);  //先清空畫面後再呼叫paint函式
	}

	//處理 TextArea 的事件
	public void textValueChanged(TextEvent e){
		//幫接收到的字串轉成整數放到 amount
		String text = txa.getText();
		String words[] = text.split("：");
		amount = Integer.valueOf(words[1]);
	}

	



	/*---------------------畫圖----------------------*/
	//在平面上畫出亂數產生的點
	public void paint(Graphics g){
		//陣列的大小由我們自己的輸入來決定
		int x[] = new int[amount];
		int y[] = new int[amount];
		List<Vertex> vertices = new Vector<Vertex>();

		//標出座標點
		for(int i=0; i<x.length; i++){
			//x y由亂數產生
			x[i] = (int)(Math.random()*441)+30;
			y[i] = (int)(Math.random()*405)+75;

			Vertex v = new Vertex(x[i], y[i]);
			vertices.add(v);  //做成一個座標陣列
			g.setColor(Color.red);  //設定點的顏色為紅色
			g.fillOval(x[i], y[i], 5, 5);  //畫出亂數座標點
		}
		List<Vertex> polygon = convexhull(vertices);  //執行 Convex Hull 演算法
		//依序將最外圍的點做連線
		for(int i=1; i<polygon.size(); i++) {
			g.setColor(Color.black);
      		g.drawLine(polygon.get(i-1).x, polygon.get(i-1).y, polygon.get(i).x, polygon.get(i).y);
    	}
    	//最後記得再把最後一個點連回第一點
    	g.setColor(Color.black);
    	g.drawLine(polygon.get(0).x, polygon.get(0).y, polygon.get(polygon.size()-1).x, polygon.get(polygon.size()-1).y);

	}
	/*-----------------------------------------------*/



	/*------執行 Convex Hull，找出凸包最外圍的點-----*/
	public static List<Vertex> convexhull(List<Vertex> vertices){
		List<Vertex> polygon = new Vector<Vertex>();
		int left, right;  //左右範圍
		int dxs,dys;  //起始斜率
    	int dxc,dyc;  //暫存斜率
    	int dxt,dyt;  //測試斜率
    	int lqc,lqt;  //長度平方(暫存/測試)
    	int compareResult;  //斜率比較結果
    	int x;  //存放數據用的
		Vertex current = null;  //目前拜訪的點
		Vertex next = null;  //下一個選擇點

		//先取出座標點的左右範圍
		left = vertices.get(0).x;
		right = vertices.get(0).x;
		for(int i=1; i<vertices.size(); i++){
			//依序取出 x 來做比較，藉此求出左右範圍
			x = vertices.get(i).x;
			if(x<left) left = x;
			if(x>right) right = x;
		}

		//找出最左上角的點，準備當成起點
		for(Vertex v : vertices){
		//依序把 vertices 加入 v
			if(v.x==left && (current==null || v.y>current.y))
				current = v;
				//再找出一個y最大的點，放到current
		}

		//將我們剛剛找到的起點加入polygon,就是最左上角的點
		polygon.add(current);

		dys = 1;  dxs = 0;  //先把起始斜率設成無限大
		while(current.x<=right){
			dyc = -1;  dxc = 0;  lqc = 0;
			for(Vertex v : vertices){
			//依序把 vertices 加入 v
				if(v.x>=current.x){
					dyt = v.y - current.y;
					dxt = v.x - current.x;

					//把起始斜率和測試斜率送到 compareSlope 函式作比較
					if(compareSlope(dyt, dxt, dys, dxs) == -1){
						compareResult = compareSlope(dyt,dxt,dyc,dxc);
						lqt = dyt*dyt+dxt*dxt;  //計算長度平方

						if(compareResult>=0)
							if(compareResult>0 || lqt>lqc){
								next = v;
								dyc = dyt;
								dxc = dxt;
								lqc = lqt;
							}
					}
				}
			}

			if(next == null) break;
			//若找到我們要的點以後，就把起始斜率換成最後一次的暫存斜率
			dys = dyc;
			dxs = dxc;
			polygon.add(next);  //把找出來的點加到polygon
			vertices.remove(next);  //把我們找過的點移除掉
			current = next;  //把next放到current，把它當成目前點位來繼續找下個點
			next = null;  //把next清空
		}


		//1. 我們首先定義起始斜率為無限大
		//2. 以 current.x 為基準來測試左邊的點
		//3. 在這些斜率中找出斜率最大的
		//4. 若有斜率相同的情況則取長度最長者
		//5. 新找到的斜率就作為下次的起始斜率
		//6. 繞完迴圈之後就可以得到全部座標中的斜率最大者
		dys = 1;  dxs = 0;
		while(current.x>left){
			dyc = -1;  dxc = 0;  lqc = 0;
			for(Vertex v : vertices){
				if(v.x<current.x){
					dyt = v.y - current.y;
					dxt = v.x - current.x;

					if(compareSlope(dyt, dxt, dys, dxs) == -1){
						compareResult = compareSlope(dyt, dxt, dyc, dxc);
						lqt = dyt*dyt+dxt*dxt;  //計算長度平方

						if(compareResult>=0)
							if(compareResult>0 || lqt>lqc){
								next = v;
								dyc = dyt;
								dxc = dxt;
								lqc = lqt;
							}
					}
				}
			}

			if(next == null) break;
			dys = dyc;
			dxs = dxc;
			polygon.add(next);
			current = next;
			next = null;
		}

		return polygon;
		//回傳我們找到的最外圍的點
	}
	/*-----------------------------------------------*/



	/*-------------------比較斜率--------------------*/
	public static int compareSlope(int dy2, int dx2, int dy1, int dx1){
		if(dx2!=0 && dx1!=0){
		//如果兩個數的斜率都不是無限的話...
			 double test = dy2*dx1-dy1*dx2;
			 return (int)Math.signum(test);
			 //若 test>0 則返回 1，若 test<0 則回傳 -1，若 test 為 0 則回傳 0
		}

		else{
			//只有其中一數的斜率是無限的時候
			if(dx2!=0 || dx1!=0){
				if(dx2 == 0){
					// dy2/dx2 斜率，無限大或無限小
					return dy2>=0 ? 1 : -1;
				}
				else{
					// dy1/dx1 斜率，無限大或無限小
					return dy1>=0 ? -1 : 1;
				}
			}

			else{
			//此情況代表兩個座標點的斜率都是無窮
				if(dy2>=0){
					// dy2/dx2 斜率，無限大或無限小
					return dy1>=0 ? 0 : 1;
				}
				else{
					// dy2/dx2 斜率，無限大或無限小
					return dy1>=0 ? -1 : 0;
				}
			}
		}
	}
	/*-----------------------------------------------*/
}