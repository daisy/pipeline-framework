package org.daisy.pipeline.persistence.jobs;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public abstract class QueryDecorator {
        CriteriaBuilder cb;        
        QueryDecorator next;

        public QueryDecorator(CriteriaBuilder cb){
                this.cb=cb;
        }


        public final QueryDecorator setNext(QueryDecorator decorator){
                this.next=decorator;
                return this.next;

        }
        public final <T> CriteriaQuery<T> getSelect(Class<T> clazz){
                CriteriaQuery<T> query=cb.createQuery(clazz);
                Root<T> e = query.from(clazz);
                //always true
                Predicate pred=cb.conjunction();
                pred=this.decorateWhere(pred,e,query);
                query.where(pred);
                return query;
        }
        

        final <T> Predicate decorateWhere(Predicate pred,Root<T> root,CriteriaQuery<T> query){
                //go through next and decorate predicates with and
                pred=this.doDecorateWhere(pred,root,query);
                if(this.next!=null){
                        pred=this.cb.and(pred,this.next.decorateWhere(pred,root,query));
                }
                return pred;
        }


        abstract <T> Predicate doDecorateWhere(Predicate pred,Root<T> root,CriteriaQuery<T> query);

}
