import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.text.DecimalFormat;

public class Simulacao {
    private static final int LotacaoMaximaEspera = 50;
    private static final int LotacaoMaximaPreferencial = 10;
    private static final int MinimoTempoTriagem = 1;
    private static final int TempoMaximoTriagem = 3;
    private static final int TempoMinimoTratamento = 2;
    private static final int TempoMaximoTratamento = 5;
    private static final int TempoMaximoEspera = 50;
    private int medicosDisponiveis;
    private int atendentesDisponiveis;


    private Queue<Paciente> SalaDeEspera;
    private Queue<Paciente> FilaVermelha;
    private Queue<Paciente> FilaAmarela;
    private Queue<Paciente> FilaVerde;
    private Queue<Paciente> FilaAzul;
    private int interrupcao;
    

    
    

    public Simulacao(int numMedicos, int numAtendentes) {
        SalaDeEspera = new LinkedList<>();
        FilaVermelha = new LinkedList<>();
        FilaAmarela = new LinkedList<>();
        FilaVerde = new LinkedList<>();
        FilaAzul = new LinkedList<>();
        interrupcao = 0;
        medicosDisponiveis = numMedicos;
        atendentesDisponiveis = numAtendentes;
    }

    public void simulate(int rounds) {
        for (int i = 1; i <= rounds; i++) {
            ingresso();
            triagem();
            atendimento();
        }

        double tempoMedioSalaEspera = CalculoTempoFilas(SalaDeEspera);
        double tempoMedioFilaVermelha = CalculoTempoFilas(FilaVermelha);
        double tempoMedioFilaAmarela = CalculoTempoFilas(FilaAmarela);
        double tempoMedioFilaVerde = CalculoTempoFilas(FilaVerde);
        double tempoMedioFilaAzul = CalculoTempoFilas(FilaAzul);

        DecimalFormat decimalFormat = new DecimalFormat("#");

        System.out.println("Quantidade de pacientes que não ficaram por lotação na sala de espera: " + SalaDeEspera.size());
        System.out.println("Tempo médio de espera na sala de espera: " + decimalFormat.format(tempoMedioSalaEspera));
        System.out.println("Tempo médio de espera na fila vermelha: " + decimalFormat.format(tempoMedioFilaVermelha));
        System.out.println("Tempo médio de espera na fila amarela: " + decimalFormat.format(tempoMedioFilaAmarela));
        System.out.println("Tempo médio de espera na fila verde: " + decimalFormat.format(tempoMedioFilaVerde));
        System.out.println("Tempo médio de espera na fila azul: " + decimalFormat.format(tempoMedioFilaAzul));
        System.out.println("Número de interrupções no ingresso: " + interrupcao);
    }

    private void ingresso() {
        Random random = new Random();
        if (random.nextDouble() < 0.5) {
            if (SalaDeEspera.size() < LotacaoMaximaEspera) {
                SalaDeEspera.add(new Paciente());
            } else {
                System.out.println("Sala de espera cheia, paciente foi embora.");
            }
        }
    }

    private void triagem() {
        Queue<Paciente> tempQueue = new LinkedList<>();
    
        for (Paciente Paciente : SalaDeEspera) {
            int Tempotriagem = getTempoAleatorio(MinimoTempoTriagem, TempoMaximoTriagem);
            Paciente.setTempoTriagem(Tempotriagem);
            Paciente.setTempoEspera(Paciente.getTempoEspera() + Tempotriagem);
            
            Risco LevelRisco = Paciente.getRiscoAleatorio();
            
            if (LevelRisco == Risco.VERMELHO) {
                FilaVermelha.add(Paciente);
            } else if (LevelRisco == Risco.AMARELO) {
                FilaAmarela.add(Paciente);
            } else if (LevelRisco == Risco.VERDE) {
                FilaVerde.add(Paciente);
            } else if (LevelRisco == Risco.AZUL) {
                FilaAzul.add(Paciente);
            }
            
            tempQueue.add(Paciente);
        }
    
        SalaDeEspera.clear();
        SalaDeEspera.addAll(tempQueue);
    }
    
    private void atendimento() {
        int pacientesPreferencial = 0;
        Queue<Paciente> currentQueue = null;
    
        while (pacientesPreferencial <= LotacaoMaximaPreferencial) {
            if (!FilaVermelha.isEmpty()) {
                currentQueue = FilaVermelha;
            } else if (!FilaAmarela.isEmpty()) {
                currentQueue = FilaAmarela;
            } else if (!FilaVerde.isEmpty()) {
                currentQueue = FilaVerde;
            } else if (!FilaAzul.isEmpty()) {
                currentQueue = FilaAzul;
            } else {
                break; // Todas as filas estão vazias, não há mais pacientes para atender
            }
    
            Paciente paciente = currentQueue.remove();
            int treatmentTime = getTempoAleatorio(TempoMinimoTratamento, TempoMaximoTratamento);
            paciente.setTempoEspera(paciente.getTempoEspera() + treatmentTime);
    
            if (paciente.getTempoEspera() > TempoMaximoEspera) {
                System.out.println("Paciente " + paciente.getId() + " ultrapassou o tempo máximo de espera e foi encaminhado para atendimento preferencial.");
                pacientesPreferencial++;
            } else {
                if (medicosDisponiveis > 0) {
                    System.out.println("Paciente " + paciente.getId() + " está sendo atendido por um médico.");
                    medicosDisponiveis--;
                } else if (atendentesDisponiveis > 0) {
                    System.out.println("Paciente " + paciente.getId() + " está sendo atendido por um atendente.");
                    atendentesDisponiveis--;
                } else {
                    System.out.println("Não há profissionais disponíveis para atender o paciente " + paciente.getId() + ".");
                }
                // Simulação do tratamento do paciente
    
                // Liberar médico ou atendente
                if (medicosDisponiveis < 3) {
                    medicosDisponiveis++;
                } else {
                    atendentesDisponiveis++;
                }
            }
            if (pacientesPreferencial > LotacaoMaximaPreferencial) {
                System.out.println("Interrupção no ingresso: " + (pacientesPreferencial - LotacaoMaximaPreferencial) + " pacientes foram encaminhados para outras unidades de atendimento.");
                interrupcao++;
            }
        }
    
        
    }
    
        
    

    private int getTempoAleatorio(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private double CalculoTempoFilas(Queue<Paciente> queue) {
        int TempoTotalDeEspera = 0;
        int TotalPacientes = queue.size();
        for (Paciente Paciente : queue) {
            TempoTotalDeEspera += Paciente.getTempoEspera();
        }
        return (double) TempoTotalDeEspera / TotalPacientes;
    }
}

