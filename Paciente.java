import java.util.Random;

public class Paciente {
    private int Id;
    private Risco risco;
    private int TempoTriagem;
    private int TempoEspera;

    private int getIdAleatoria() {
        Random random = new Random();
        return random.nextInt();
    }

    public Paciente() {
        Id = getIdAleatoria();
        risco = getRiscoAleatorio();
        TempoTriagem = 0;
        TempoEspera = 0;
    }

    public int getId() {
        return Id;
    }

    public Risco getRisco() {
        return risco;
    }

    public int getTempoTriagem() {
        return TempoTriagem;
    }

    public void setTempoTriagem (int TempoTriagem) {
        this.TempoTriagem = TempoTriagem;
    }

    public int getTempoEspera() {
        return TempoEspera;
    }

    public void setTempoEspera(int TempoEspera) {
        this.TempoEspera = TempoEspera;
    }

    public Risco getRiscoAleatorio() {
        Risco[] Riscos = Risco.values();
        int randomIndex = (int) (Math.random() * Riscos.length);
        return Riscos[randomIndex];
    }
}
