package PPDCorba;


/**
* PPDCorba/NoSuchPart.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ppdPecas.idl
* sexta-feira, 19 de abril de 2019 00:04:39 Horário Padrão de Brasília
*/

public final class NoSuchPart extends org.omg.CORBA.UserException
{
  public String partCode = null;

  public NoSuchPart ()
  {
    super(NoSuchPartHelper.id());
  } // ctor

  public NoSuchPart (String _partCode)
  {
    super(NoSuchPartHelper.id());
    partCode = _partCode;
  } // ctor


  public NoSuchPart (String $reason, String _partCode)
  {
    super(NoSuchPartHelper.id() + "  " + $reason);
    partCode = _partCode;
  } // ctor

} // class NoSuchPart