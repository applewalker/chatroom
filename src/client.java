import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
public class client extends JFrame implements Runnable {
	JFrame jfr1=new JFrame("��¼");
	JButton jfb1=new JButton("ENTER");
	JButton post=new JButton("post");
	JButton clear1=new JButton("clear");
	JButton clear2=new JButton("clear");
	JFrame jfr2=new JFrame("������");
	JPanel jpa1=new JPanel();
	JPanel jpa2=new JPanel();
	JTextArea marea= new JTextArea();//Ⱥ�����ݿ�
	JTextArea parea= new JTextArea();//˽�����ݿ�
	JTextField sfield= new JTextField();//�����
	JLabel cr=new JLabel("ChatRoom");
	JLabel nlabel1=new JLabel("USERNAME��");//��½������
	JLabel publicc=new JLabel("��������");
	JLabel privatee=new JLabel("˽������           (ʹ�÷���:\"@�û�ID@��������\")");
	JTextField nfield1=new JTextField();
	JLabel nlabel2=new JLabel("USERNAME��");
	JTextField nfield2=new JTextField();//��������
	JScrollPane jsc=new JScrollPane();//����Ⱥ�Ŀ�
	JScrollPane jsc2=new JScrollPane();//����˽�Ŀ�
	BufferedReader in;
	PrintWriter out;
	Socket socket;
public void interf(){
	jfr1.setBounds(550,240,300,300);//��¼����
	jfr1.setVisible(true);
	jfr1.getContentPane().add(jpa1);
	jpa1.setLayout(null);
	jpa1.add(jfb1);
	jfb1.setBounds(100, 180, 100, 18);//ENTER��ť
	jpa1.add(nfield1);
	nfield1.setBounds(123,130,150,20);//USERNAME��
	jpa1.add(nlabel1);
	jpa1.add(cr);
	cr.setBounds(40, 50, 280, 30);
	nlabel1.setBounds(23,130,80,20);
	cr.setFont(new  java.awt.Font("Copperplate Gothic Light",1,35));  
	
	jfr2.setBounds(500,30,365,560);//���������ҳ�ʼλ�úʹ��ڴ�С
	jfr2.getContentPane().add(jpa2);
	jpa2.setLayout(null);
	marea.setEditable(false);//���ɱ���marea
	marea.setColumns(25);
	marea.setRows(5);
	marea.setLineWrap(true);//���еĳ��ȴ���������Ŀ��ʱ�������С�
	parea.setEditable(false);
	parea.setColumns(25);
	parea.setRows(5);
	parea.setLineWrap(true);
	jsc.setViewportView(marea);
	jsc2.setViewportView(parea);
	
	buttonlooks(post);
	buttonlooks(clear1);
	buttonlooks(clear2);
	publicc.setBounds(15, 7, 80, 20);
	jsc.setBounds(15,30,330,240);
	privatee.setBounds(15,290,330,20);
	jsc2.setBounds(15,310,330,150);
	nlabel2.setBounds(15,470,80,20);
	nfield2.setBounds(95,470,100,20);//USERNAME��
	sfield.setBounds(15,500,255,25);//�����
	post.setBounds(285, 502, 60, 20);
	clear1.setBounds(284, 270, 60, 20);
	clear2.setBounds(284,460,60,20);
	
	jpa2.add(jsc);
	jpa2.add(jsc2);
	jpa2.add(sfield);
	jpa2.add(post);
	jpa2.add(clear1);
	jpa2.add(clear2);
	jpa2.add(publicc);
	jpa2.add(privatee);
	jpa2.add(nfield2);
	jpa2.add(nlabel2);
	
	jfr2.addWindowListener(new WindowAdapter() { // ���û��˳�ʱ����Ҫ֪ͨ�����ˣ������˳�   
        public void windowClosing(WindowEvent arg0) {  
            sendExitMsg();  
        }  
    });  
	actlisten a=new actlisten();
	jfb1.addActionListener(a);
	nfield1.addActionListener(a);
	sfield.addActionListener(a);
	post.addActionListener(a);
	clear1.addActionListener(a);
	clear2.addActionListener(a);
	jfr1.setResizable(false);//��ֹ�޸Ĵ��ڴ�С
	jfr2.setResizable(false);
	}
class actlisten implements ActionListener{
public void actionPerformed(ActionEvent e){
	if(e.getSource()==sfield || e.getSource()==post){//�û�����������Ϣ
	String check=sfield.getText();
	String ss=time()+" "+nfield2.getText()+"��\n"+check;
	if(check.startsWith("@")){//˽������
		out.println(check);
	}
	else{
	out.println(ss);
	}
	sfield.setText("");
	}
    else if(e.getSource()==nfield1 || e.getSource()==jfb1){
    	if(nfield1.getText().equals(""))//�û��������ֲ���Ϊ��
        {JOptionPane.showMessageDialog(null, 
				"�������û�����", "����", JOptionPane.ERROR_MESSAGE);
        }
    	else
    		getsocket();
		}
    else if(e.getSource()==clear1)
    {
    	marea.setText("");
    }
    else if(e.getSource()==clear2)
    {
    	parea.setText("");
    }
	}
}
private void sendExitMsg() {  //�˳���Ϣ
    if (socket == null) {  
        System.exit(0);  
        return;  
    }  
    String exitmsg = ">>  " + nfield1.getText()+"  ���˳�";//֪ͨ����������   
    out.println(exitmsg);  
    System.exit(0);  
}  
public void getsocket(){
	try{
		socket=new Socket("127.0.0.1",7000);//���ػ�������ʱע���޸�
		in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new PrintWriter(socket.getOutputStream(),true);
		String username="#"+nfield1.getText();
		out.println(username);//����USERNAME֪ͨ����
		String backmsg=in.readLine();
		if(backmsg.equals("#error01")){
				JOptionPane.showMessageDialog(null, 
						"�û����Ѵ���", "����", JOptionPane.ERROR_MESSAGE);
			}  
		else
			{	
				nfield2.setEditable(false);//USERNAME�����ٸ���
				nfield2.setText(nfield1.getText());
				marea.append("�����ӵ�������\n");
				new Thread(this).start();
				jfr1.setVisible(false);
				jfr2.setVisible(true);//��ʾ������		
			}
	}catch(Exception e){
		JOptionPane.showMessageDialog(null, 
				"�޷����ӵ�������", "ʧȥ����", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
}
public void run(){
	while (true){
		try{
			String msg=in.readLine();
			if(msg.startsWith("##[˽��]")){
				parea.append(msg.substring(2)+"\n");
				parea.setCaretPosition(parea.getText().length());
			}
			else{
			marea.append(msg+"\n");
			marea.setCaretPosition(marea.getText().length());//�����������ʾ��λ
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, 
					"��������Ͽ�,��ȷ��5�������", "ʧȥ����", JOptionPane.INFORMATION_MESSAGE);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} 
				getsocket();
			e.printStackTrace();
		}
	}	
}
public void buttonlooks(JButton a){
	a.setBackground(Color.white);//��ťͳһ��ʽ
	a.setBorder(BorderFactory.createLineBorder(Color.black));
}
public String time(){//��¼ʱ��
	Date dt=new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//������ʾ��ʽ
	return df.format(dt);
}
public static void main(String[] args){
	(new client()).interf();
} 
}