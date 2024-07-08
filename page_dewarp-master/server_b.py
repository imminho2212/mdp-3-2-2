from flask import Flask, send_file
import os
import signal
import glob
app = Flask(__name__)

@app.route('/download_folder')
def download_folder():
    folder_path = '/home/pi/mdp/page_dewarp-master/output.pdf'  # 전송하려는 폴더의 경로
    return send_file(folder_path,mimetype='application/pdf', as_attachment=True)

@app.route('/shutdown')
def shutdown():
   print("Shutting down gracefully...")
   os.system("touch /home/pi/mdp/page_dewarp-master/qwe/end.txt")
   os.kill(os.getpid(), signal.SIGINT)
   return 'Server shutting down...'

@app.route('/stop')
def stop():
    os.system("rm /home/pi/mdp/page_dewarp-master/command/take.txt")
    return ''

@app.route('/start_scan/<int_value>')
def int_make(int_value):
    os.system("touch /home/pi/mdp/page_dewarp-master/command/take.txt")
    ivalue = int_value
    [os.remove(i) for i in glob.glob('/home/pi/mdp/page_dewarp-master/pagenum/*.txt')]
    os.system('touch /home/pi/mdp/page_dewarp-master/pagenum/{}.txt'.format(ivalue))
    return ''

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0',port=5000)
