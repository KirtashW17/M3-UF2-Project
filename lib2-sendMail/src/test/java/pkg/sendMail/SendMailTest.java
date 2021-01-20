package pkg.sendMail;

import org.junit.Test;
import org.junit.Assert.*;


//Probando TDD con JUnit
public class SendMailTest {

    @Test
    public void testSendMail() throws Exception {
        int result;
        result = SendMail.sendMail("test@example.com", "Correo de Prueba _ 2", "Correol de Prueba");
        assert result==0;
    }

    @Test
    public void testSendMailBadAddress() throws Exception {
        int result;
        result = SendMail.sendMail("test", "Correo de Prueba _ 2", "Correol de Prueba");
        assert result==1;
    }

    @Test
    public void testAttachedString() throws Exception {
        final String attachedFilePath = "/etc/hosts";
        int result;
        result = SendMail.sendMail("test@example.com", "Correo de Prueba _ 2", "Correo de Prueba", "attachedFilePath");
        assert result==0;
    }
}
