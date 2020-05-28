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
	static TextArea txa = new TextArea("�п�J�n�X���I�G");
	static TextArea txa1 = new TextArea("Convex Hull");
	static Label lab1 = new Label("Convex Hull");
	int amount;  //�����Ѥ�r�����J���Ʀr

	//�w�q�@�� Vertex ���O�A��m x y �Φ��@�Ӯy��
	private static class Vertex {
		public int x;
		public int y;

		//�غc�l�A�i�H�������ө� x y
		public Vertex(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
  	/*----------------------------------------------*/



	/*-------------�{���� Entry Point---------------*/
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



	/*--------------------�B�z�ƥ�-------------------*/
	//�B�z button ���ƥ�
	public void actionPerformed(ActionEvent e){
		Graphics g = getGraphics();
		update(g);  //���M�ŵe����A�I�spaint�禡
	}

	//�B�z TextArea ���ƥ�
	public void textValueChanged(TextEvent e){
		//�������쪺�r���ন��Ʃ�� amount
		String text = txa.getText();
		String words[] = text.split("�G");
		amount = Integer.valueOf(words[1]);
	}

	



	/*---------------------�e��----------------------*/
	//�b�����W�e�X�üƲ��ͪ��I
	public void paint(Graphics g){
		//�}�C���j�p�ѧڭ̦ۤv����J�ӨM�w
		int x[] = new int[amount];
		int y[] = new int[amount];
		List<Vertex> vertices = new Vector<Vertex>();

		//�ХX�y���I
		for(int i=0; i<x.length; i++){
			//x y�ѶüƲ���
			x[i] = (int)(Math.random()*441)+30;
			y[i] = (int)(Math.random()*405)+75;

			Vertex v = new Vertex(x[i], y[i]);
			vertices.add(v);  //�����@�Ӯy�а}�C
			g.setColor(Color.red);  //�]�w�I���C�⬰����
			g.fillOval(x[i], y[i], 5, 5);  //�e�X�üƮy���I
		}
		List<Vertex> polygon = convexhull(vertices);  //���� Convex Hull �t��k
		//�̧ǱN�̥~���I���s�u
		for(int i=1; i<polygon.size(); i++) {
			g.setColor(Color.black);
      		g.drawLine(polygon.get(i-1).x, polygon.get(i-1).y, polygon.get(i).x, polygon.get(i).y);
    	}
    	//�̫�O�o�A��̫�@���I�s�^�Ĥ@�I
    	g.setColor(Color.black);
    	g.drawLine(polygon.get(0).x, polygon.get(0).y, polygon.get(polygon.size()-1).x, polygon.get(polygon.size()-1).y);

	}
	/*-----------------------------------------------*/



	/*------���� Convex Hull�A��X�Y�]�̥~���I-----*/
	public static List<Vertex> convexhull(List<Vertex> vertices){
		List<Vertex> polygon = new Vector<Vertex>();
		int left, right;  //���k�d��
		int dxs,dys;  //�_�l�ײv
    	int dxc,dyc;  //�Ȧs�ײv
    	int dxt,dyt;  //���ձײv
    	int lqc,lqt;  //���ץ���(�Ȧs/����)
    	int compareResult;  //�ײv������G
    	int x;  //�s��ƾڥΪ�
		Vertex current = null;  //�ثe���X���I
		Vertex next = null;  //�U�@�ӿ���I

		//�����X�y���I�����k�d��
		left = vertices.get(0).x;
		right = vertices.get(0).x;
		for(int i=1; i<vertices.size(); i++){
			//�̧Ǩ��X x �Ӱ�����A�Ǧ��D�X���k�d��
			x = vertices.get(i).x;
			if(x<left) left = x;
			if(x>right) right = x;
		}

		//��X�̥��W�����I�A�ǳƷ��_�I
		for(Vertex v : vertices){
		//�̧ǧ� vertices �[�J v
			if(v.x==left && (current==null || v.y>current.y))
				current = v;
				//�A��X�@��y�̤j���I�A���current
		}

		//�N�ڭ̭���쪺�_�I�[�Jpolygon,�N�O�̥��W�����I
		polygon.add(current);

		dys = 1;  dxs = 0;  //����_�l�ײv�]���L���j
		while(current.x<=right){
			dyc = -1;  dxc = 0;  lqc = 0;
			for(Vertex v : vertices){
			//�̧ǧ� vertices �[�J v
				if(v.x>=current.x){
					dyt = v.y - current.y;
					dxt = v.x - current.x;

					//��_�l�ײv�M���ձײv�e�� compareSlope �禡�@���
					if(compareSlope(dyt, dxt, dys, dxs) == -1){
						compareResult = compareSlope(dyt,dxt,dyc,dxc);
						lqt = dyt*dyt+dxt*dxt;  //�p����ץ���

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
			//�Y���ڭ̭n���I�H��A�N��_�l�ײv�����̫�@�����Ȧs�ײv
			dys = dyc;
			dxs = dxc;
			polygon.add(next);  //���X�Ӫ��I�[��polygon
			vertices.remove(next);  //��ڭ̧�L���I������
			current = next;  //��next���current�A�⥦���ثe�I����~���U���I
			next = null;  //��next�M��
		}


		//1. �ڭ̭����w�q�_�l�ײv���L���j
		//2. �H current.x ����ǨӴ��ե��䪺�I
		//3. �b�o�Ǳײv����X�ײv�̤j��
		//4. �Y���ײv�ۦP�����p�h�����׳̪���
		//5. �s��쪺�ײv�N�@���U�����_�l�ײv
		//6. ¶���j�餧��N�i�H�o������y�Ф����ײv�̤j��
		dys = 1;  dxs = 0;
		while(current.x>left){
			dyc = -1;  dxc = 0;  lqc = 0;
			for(Vertex v : vertices){
				if(v.x<current.x){
					dyt = v.y - current.y;
					dxt = v.x - current.x;

					if(compareSlope(dyt, dxt, dys, dxs) == -1){
						compareResult = compareSlope(dyt, dxt, dyc, dxc);
						lqt = dyt*dyt+dxt*dxt;  //�p����ץ���

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
		//�^�ǧڭ̧�쪺�̥~���I
	}
	/*-----------------------------------------------*/



	/*-------------------����ײv--------------------*/
	public static int compareSlope(int dy2, int dx2, int dy1, int dx1){
		if(dx2!=0 && dx1!=0){
		//�p�G��Ӽƪ��ײv�����O�L������...
			 double test = dy2*dx1-dy1*dx2;
			 return (int)Math.signum(test);
			 //�Y test>0 �h��^ 1�A�Y test<0 �h�^�� -1�A�Y test �� 0 �h�^�� 0
		}

		else{
			//�u���䤤�@�ƪ��ײv�O�L�����ɭ�
			if(dx2!=0 || dx1!=0){
				if(dx2 == 0){
					// dy2/dx2 �ײv�A�L���j�εL���p
					return dy2>=0 ? 1 : -1;
				}
				else{
					// dy1/dx1 �ײv�A�L���j�εL���p
					return dy1>=0 ? -1 : 1;
				}
			}

			else{
			//�����p�N���Ӯy���I���ײv���O�L�a
				if(dy2>=0){
					// dy2/dx2 �ײv�A�L���j�εL���p
					return dy1>=0 ? 0 : 1;
				}
				else{
					// dy2/dx2 �ײv�A�L���j�εL���p
					return dy1>=0 ? -1 : 0;
				}
			}
		}
	}
	/*-----------------------------------------------*/
}