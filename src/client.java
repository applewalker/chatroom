import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
public class client extends JFrame implements Runnable {
	JFrame jfr1=new JFrame("登录");
	JButton jfb1=new JButton("ENTER");
	JButton post=new JButton("post");
	JButton clear1=new JButton("clear");
	JButton clear2=new JButton("clear");
	JFrame jfr2=new JFrame("聊天室");
	JPanel jpa1=new JPanel();
	JPanel jpa2=new JPanel();
	JTextArea marea= new JTextArea();//群聊内容框
	JTextArea parea= new JTextArea();//私聊内容框
	JTextField sfield= new JTextField();//输入框
	JLabel cr=new JLabel("ChatRoom");
	JLabel nlabel1=new JLabel("USERNAME：");//登陆界面用
	JLabel publicc=new JLabel("公共聊天");
	JLabel privatee=new JLabel("私人聊天           (使用方法:\"@用户ID@聊天内容\")");
	JTextField nfield1=new JTextField();
	JLabel nlabel2=new JLabel("USERNAME：");
	JTextField nfield2=new JTextField();//聊天室用
	JScrollPane jsc=new JScrollPane();//用于群聊框
	JScrollPane jsc2=new JScrollPane();//用于私聊框
	BufferedReader in;
	PrintWriter out;
	Socket socket;
public void interf(){
	jfr1.setBounds(550,240,300,300);//登录界面
	jfr1.setVisible(true);
	jfr1.getContentPane().add(jpa1);
	jpa1.setLayout(null);
	jpa1.add(jfb1);
	jfb1.setBounds(100, 180, 100, 18);//ENTER按钮
	jpa1.add(nfield1);
	nfield1.setBounds(123,130,150,20);//USERNAME框
	jpa1.add(nlabel1);
	jpa1.add(cr);
	cr.setBounds(40, 50, 280, 30);
	nlabel1.setBounds(23,130,80,20);
	cr.setFont(new  java.awt.Font("Copperplate Gothic Light",1,35));  
	
	jfr2.setBounds(500,30,365,560);//设置聊天室初始位置和窗口大小
	jfr2.getContentPane().add(jpa2);
	jpa2.setLayout(null);
	marea.setEditable(false);//不可编译marea
	marea.setColumns(25);
	marea.setRows(5);
	marea.setLineWrap(true);//当行的长度大于所分配的宽度时，将换行。
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
	nfield2.setBounds(95,470,100,20);//USERNAME框
	sfield.setBounds(15,500,255,25);//输入框
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
	
	jfr2.addWindowListener(new WindowAdapter() { // 当用户退出时，先要通知所有人，有人退出   
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
	jfr1.setResizable(false);//阻止修改窗口大小
	jfr2.setResizable(false);
	}
class actlisten implements ActionListener{
public void actionPerformed(ActionEvent e){
	if(e.getSource()==sfield || e.getSource()==post){//用户输入聊天信息
	String check=sfield.getText();
	String ss=time()+" "+nfield2.getText()+"：\n"+check;
	if(check.startsWith("@")){//私聊请求
		out.println(check);
	}
	else{
	out.println(ss);
	}
	sfield.setText("");
	}
    else if(e.getSource()==nfield1 || e.getSource()==jfb1){
    	if(nfield1.getText().equals(""))//用户输入名字不能为空
        {JOptionPane.showMessageDialog(null, 
				"请输入用户名字", "警告", JOptionPane.ERROR_MESSAGE);
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
private void sendExitMsg() {  //退出信息
    if (socket == null) {  
        System.exit(0);  
        return;  
    }  
    String exitmsg = ">>  " + nfield1.getText()+"  已退出";//通知所有人下线   
    out.println(exitmsg);  
    System.exit(0);  
}  
public void getsocket(){
	try{
		socket=new Socket("127.0.0.1",7000);//本地机，测试时注意修改
		in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new PrintWriter(socket.getOutputStream(),true);
		String username="#"+nfield1.getText();
		out.println(username);//发送USERNAME通知上线
		String backmsg=in.readLine();
		if(backmsg.equals("#error01")){
				JOptionPane.showMessageDialog(null, 
						"用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
			}  
		else
			{	
				nfield2.setEditable(false);//USERNAME不可再更改
				nfield2.setText(nfield1.getText());
				marea.append("已连接到服务器\n");
				new Thread(this).start();
				jfr1.setVisible(false);
				jfr2.setVisible(true);//显示聊天室		
			}
	}catch(Exception e){
		JOptionPane.showMessageDialog(null, 
				"无法连接到服务器", "失去连接", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
}
public void run(){
	while (true){
		try{
			String msg=in.readLine();
			if(msg.startsWith("##[私聊]")){
				parea.append(msg.substring(2)+"\n");
				parea.setCaretPosition(parea.getText().length());
			}
			else{
			marea.append(msg+"\n");
			marea.setCaretPosition(marea.getText().length());//光标在最新显示部位
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, 
					"与服务器断开,请确定5秒后重连", "失去连接", JOptionPane.INFORMATION_MESSAGE);
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
	a.setBackground(Color.white);//按钮统一样式
	a.setBorder(BorderFactory.createLineBorder(Color.black));
}
public String time(){//记录时间
	Date dt=new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//设置显示格式
	return df.format(dt);
}
public static void main(String[] args){
	(new client()).interf();
} 
}