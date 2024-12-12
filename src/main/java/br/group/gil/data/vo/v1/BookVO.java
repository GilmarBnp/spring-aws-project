package br.group.gil.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.hateoas.RepresentationModel;

public class BookVO extends RepresentationModel<BookVO> implements Serializable {
		private static final long serialVersionUID = 1L;

		public Long id;
		public String author;
		public LocalDate launchDate;
		public Double price;
		public String title;
		
		public BookVO(){}
			
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public LocalDate getLaunchDate() {
			return launchDate;
		}
		public void setLaunchDate(LocalDate launchDate) {
			this.launchDate = launchDate;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		@Override
		public int hashCode() {
			return Objects.hash(author, id, launchDate, price, title);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BookVO other = (BookVO) obj;
			return Objects.equals(author, other.author) && id == other.id
					&& Objects.equals(launchDate, other.launchDate) && Objects.equals(price, other.price)
					&& Objects.equals(title, other.title);
		}
		
	}



