package nl.illuminated.arduino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;

public class ArduinoPort
{

    private static SerialPort serialPort;
    private BlockingQueue rxQueue;
    private boolean keepRunning;
    private BufferedReader reader;
    private final SerialPortReader readerThread;
    private final Object arduinoLock;
    private int rowsReceived;
    
    private SerialPort initSerialPort(String port, int baudrate) throws NoSuchPortException, PortInUseException
    {
        CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(port);
        SerialPort mySerialPort = (SerialPort) identifier.open("Arduino", baudrate);
        return mySerialPort;
    }
    public void RequestStop()
    {
        keepRunning = false;
    }

    private ArduinoPort(String port, int baudrate) throws NoSuchPortException, PortInUseException
    {
        rxQueue = new LinkedBlockingQueue<>();
        keepRunning = true;
        arduinoLock = new Object();
        this.serialPort = initSerialPort(port, baudrate);
        try
        {
            reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        }
        catch (IOException ex)
        {
            Logger.getLogger(ArduinoPort.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.readerThread = new SerialPortReader(reader);
        this.readerThread.setDaemon(false);
    }

    public void start()
    {
        readerThread.start();
        keepRunning = true;
        handleReceivedPackages();
    }

    public void handleReceivedPackages()
    {
        String row;
        while (keepRunning)
        {
            try
            {
                row = (String) rxQueue.take();
                System.out.println("Gelezen data : " + row);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ArduinoPort.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            ArduinoPort port = new ArduinoPort("COM6", 9600);//COM1 of COM2 of COM3
            port.start();
        }
        catch (NoSuchPortException | PortInUseException ex)
        {
            Logger.getLogger(ArduinoPort.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class SerialPortReader extends Thread
    {

        private final BufferedReader myReader;

        public SerialPortReader(BufferedReader reader)
        {
            this.myReader = reader;
        }

        public void run()
        {
            while (keepRunning)
            {
                try
                {
                    String line = myReader.readLine();
                    rxQueue.put(line);
                }
                catch (IOException | InterruptedException ex)
                {
                    Logger.getLogger(ArduinoPort.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
