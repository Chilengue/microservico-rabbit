package com.example.demo.connections;

import org.springframework.stereotype.Component;

import com.example.demo.constant.RabbitmqConstants;

import jakarta.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

@Component
public class RabbitmqConnection {

    private static final String Nome_EXCHANGE ="amq.direct";
    private AmqpAdmin amqpAdmin;

    public RabbitmqConnection(AmqpAdmin amqpAdmin){
        this.amqpAdmin=amqpAdmin;
    }

    private Queue fila(String nomeFila){
        return new Queue(nomeFila, true, false, false);
    }
    private DirectExchange trocarDireto()
    {
        return new DirectExchange(Nome_EXCHANGE);
    }
    private Binding relacionamento(Queue fila, DirectExchange troca){
      return  new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
    }

    @PostConstruct
    private void adiciona (){
       Queue filaEstoque= this.fila(RabbitmqConstants.FILA_ESTOQUE);
       Queue filaPreco =this.fila(RabbitmqConstants.FILA_PRECO);

       DirectExchange troca=this.trocarDireto();

       this.relacionamento(filaEstoque, troca);

       Binding ligacaoEstoque = this.relacionamento(filaEstoque, troca);
       Binding ligacaoPreco  = this.relacionamento(filaPreco   , troca);

       //criando relacionamento no RabbiMq
       this.amqpAdmin.declareQueue(filaEstoque);
       this.amqpAdmin.declareQueue(filaPreco);

       this.amqpAdmin.declareExchange(troca);

       this.amqpAdmin.declareBinding(ligacaoEstoque);
       this.amqpAdmin.declareBinding(ligacaoPreco);
    }
    
    
}
